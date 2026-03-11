package com.courier.management.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.courier.management.dto.InvoiceDTO;
import com.courier.management.dto.ResponseDTO;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.Entry;
import com.courier.management.entity.Invoice;
import com.courier.management.entity.InvoiceSequence;
import com.courier.management.enums.AnyStatus;
import com.courier.management.helper.InvoiceHelper;
import com.courier.management.projection.EntryProjection;
import com.courier.management.projection.InvProjection;
import com.courier.management.repository.ClientRepository;
import com.courier.management.repository.CustomerRepository;
import com.courier.management.repository.EntryRepository;
import com.courier.management.repository.InvoiceRepository;
import com.courier.management.repository.InvoiceSequenceRepository;
import com.courier.management.repository.StateRepository;
import com.courier.management.util.AmountToWordsUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServices {

        private final EntryRepository entryRepository;
        private final InvoiceRepository invoiceRepository;
        private final InvoiceHelper invoiceHelper;
        private final CustomerRepository customerRepository;
        private final ClientRepository clientRepository;
        private final InvoiceSequenceRepository invoiceSequenceRepository;

        public void genInvoice(InvoiceDTO dto) {

                List<Entry> entries = invoiceHelper.getAwbData(
                                dto.getCustid(),
                                dto.getClientid(),
                                dto.getInvDateFrom(),
                                dto.getInvDateTo());

                if (entries.isEmpty()) {
                        throw new RuntimeException("No valid AWB entries found");
                }

                // ---------- Base Amount ----------
                BigDecimal baseAmount = entries.stream()
                                .map(Entry::getCharge)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                long awbCount = entries.size();

                // ---------- Load Customer & Client ----------
                Customer customer = customerRepository.findById(dto.getCustid())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                Client client = clientRepository.findById(dto.getClientid())
                                .orElseThrow(() -> new RuntimeException("Client not found"));

                // ---------- Rates ----------
                BigDecimal fuelRate = customer.getFuelRate();
                BigDecimal discountRate = new BigDecimal(dto.getDiscount());

                // ---------- Fuel Amount ----------
                BigDecimal fuelAmt = baseAmount
                                .multiply(fuelRate)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                // ---------- Discount Amount ----------
                BigDecimal discountAmt = baseAmount
                                .multiply(discountRate)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                // ---------- Taxable Amount ----------
                BigDecimal taxableAmt = baseAmount
                                .add(fuelAmt)
                                .subtract(discountAmt);

                // ---------- GST ----------
                BigDecimal cgst = BigDecimal.ZERO;
                BigDecimal sgst = BigDecimal.ZERO;
                BigDecimal igst = BigDecimal.ZERO;

                if (customer.getIsGst()) {

                        BigDecimal gstRate = client.getTaxRate();

                        if (!customer.getState().equals(client.getState())) {

                                igst = taxableAmt
                                                .multiply(gstRate)
                                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                        } else {

                                BigDecimal halfGst = gstRate
                                                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

                                cgst = taxableAmt
                                                .multiply(halfGst)
                                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                                sgst = cgst;
                        }
                }

                // ---------- Final Amount ----------
                BigDecimal invoiceTotal = taxableAmt
                                .add(cgst)
                                .add(sgst)
                                .add(igst);

                // ---------- Save Invoice ----------
                Invoice invoice = new Invoice();

                invoice.setInvDate(dto.getInvDate());
                invoice.setInvDateFrom(dto.getInvDateFrom());
                invoice.setInvDateTo(dto.getInvDateTo());

                invoice.setCustomer(customer);
                invoice.setClient(client);

                invoice.setAwbCount(awbCount);

                invoice.setInvBaseAmt(baseAmount);
                invoice.setFuelAmt(fuelAmt);
                invoice.setDiscountAmt(discountAmt);

                invoice.setCgstAmt(cgst);
                invoice.setSgstAmt(sgst);
                invoice.setIgstAmt(igst);

                invoice.setInvAmt(invoiceTotal);
                invoice.setInvNo(dto.getInvNo());

                invoice.setIsCancel(false); // on create there is no cancel || default but still

                Invoice savedInvoice = invoiceRepository.save(invoice);

                // ---------- Update Entries ----------
                entries.forEach(e -> e.setInvoice(savedInvoice));
                entryRepository.saveAll(entries);

                // ---------- Update Invoice Sequence ----------
                String[] invParts = dto.getInvNo().split("/");

                InvoiceSequence invoiceSequence = invoiceSequenceRepository
                                .findByFy(invParts[0])
                                .orElseThrow(() -> new RuntimeException("Invoice Sequencer Failed"));

                if ("INV".equals(invParts[1])) {

                        invoiceSequence.setGstLast(invoiceSequence.getGstLast() + 1);

                } else {

                        invoiceSequence.setNgLast(invoiceSequence.getNgLast() + 1);
                }

                invoiceSequenceRepository.save(invoiceSequence);

        }

        // public InvProjection isInvStatus(String inv_no) {
        // String inv = inv_no.trim().toUpperCase(); // be safer side

        // InvProjection optentry = invoiceRepository.findByInvNoS(inv);

        // return optentry;
        // }

        // public void deleteInv(String inv_no) {

        // String inv = inv_no.trim().toUpperCase(); // be safer side

        // Invoice invoice = invoiceRepository.findByInvNo(inv)
        // .orElseThrow(() -> new RuntimeException("Invoice Couldn't found, Try
        // Printing"));

        // List<Entry> entrylist = entryRepository.findByInvoice(invoice);

        // // ---------- Update Entries ---------

        // if (entrylist.isEmpty()) {
        // throw new RuntimeException("Something Went wrong");
        // }

        // entrylist.forEach(e -> e.setInvoice(null));
        // entryRepository.saveAll(entrylist);
        // invoiceRepository.deleteById(invoice.getInvId());

        // // above all run or none

        // }

        // Cancel Invoice Not Delete it Safe For audit
        public void cancelInv(String inv_no) {

                String inv = inv_no.trim().toUpperCase(); // be safer side

                Invoice invoice = invoiceRepository.findByInvNo(inv)
                                .orElseThrow(() -> new RuntimeException("Invoice Couldn't found"));

                if (invoice.getIsCancel().equals(true)) {
                        throw new RuntimeException("Already Canceled");
                }

                List<Entry> entrylist = entryRepository.findByInvoice(invoice);

                // ---------- Update Entries ---------

                if (entrylist.isEmpty()) {
                        throw new RuntimeException("Something Went wrong");
                }

                entrylist.forEach(e -> e.setInvoice(null));
                entryRepository.saveAll(entrylist);
                // invoiceRepository.deleteById(invoice.getInvId());
                invoice.setIsCancel(true);

                invoiceRepository.save(invoice);

                // above all run or none

        }

        // Sending Response DTO

        public ResponseDTO fetchInv(String inv_no) {

                String inv = inv_no.trim().toUpperCase(); // be safer side

                Invoice invoice = invoiceRepository.findByInvNo(inv)
                                .orElseThrow(() -> new RuntimeException("Invoice Couldn't found, Try Viewing"));
                System.out.println(invoice.getInvId());

                Customer customer = invoice.getCustomer();
                Client client = invoice.getClient();
                String custState = customer.getState().getStateName();
                String clientState = client.getState().getStateName();
                List<EntryProjection> eProjection = entryRepository.findEntriesByInvoice(invoice.getInvId());

                ResponseDTO rDto = new ResponseDTO();

                // assigning
                // Invoice basic details
                rDto.setInvNo(invoice.getInvNo());
                rDto.setInvDate(invoice.getInvDate());
                rDto.setInvDateFrom(invoice.getInvDateFrom());
                rDto.setInvDateTo(invoice.getInvDateTo());

                // Invoice amounts
                rDto.setCgstAmt(invoice.getCgstAmt());
                rDto.setSgstAmt(invoice.getSgstAmt());
                rDto.setIgstAmt(invoice.getIgstAmt());
                rDto.setFuelAmt(invoice.getFuelAmt());
                rDto.setDiscountAmt(invoice.getDiscountAmt());
                rDto.setInvBaseAmt(invoice.getInvBaseAmt());
                rDto.setInvAmt(invoice.getInvAmt());
                rDto.setAmtWord(AmountToWordsUtil.convert(invoice.getInvAmt()));

                // Customer part
                rDto.setCustName(customer.getCustName());
                rDto.setCustGst(customer.getCustGst() == null ? "" : customer.getCustGst());
                rDto.setCustAdd(customer.getCustAdd());
                rDto.setCuststateName(custState);
                rDto.setCustPin(customer.getCustPin());
                rDto.setIsGst(customer.getIsGst());
                rDto.setDiscountRate(customer.getDiscountRate());
                rDto.setFuelRate(customer.getFuelRate());

                // Client (Owner) part
                rDto.setClientName(client.getClientName());
                rDto.setClientAdd(client.getClientAdd());
                rDto.setClientPin(client.getClientPin());
                rDto.setClientGstin(client.getClientGstin());
                rDto.setClientstateName(clientState);
                System.out.println(eProjection.size());
                // Check for Validation
                if (eProjection.size() != invoice.getAwbCount()) {
                        throw new RuntimeException("Something Went missmatch awb count Wrong");
                }

                // Entry list
                rDto.setEntryList(eProjection);
                return rDto;

        }

        // Jasper formating here

        public byte[] generateInvoice(String inv_no) throws Exception {

                // Data Collection
                String inv = inv_no.trim().toUpperCase(); // be safer side

                Invoice invoice = invoiceRepository.findByInvNo(inv)
                                .orElseThrow(() -> new RuntimeException("Invoice Couldn't found."));
                // System.out.println(invoice.getInvId());

                if (invoice.getIsCancel().equals(true)) {
                        throw new RuntimeException("Canceled Invoice");
                }

                Customer customer = invoice.getCustomer();
                Client client = invoice.getClient();
                String custState = customer.getState().getStateCode();
                String clientState = client.getState().getStateCode();
                String cleintStno = client.getState().getGstScode();
                String custStno = customer.getState().getGstScode();
                List<EntryProjection> eProjection = entryRepository.findEntriesByInvoice(invoice.getInvId());

                // Check for Validation
                if (eProjection.size() != invoice.getAwbCount()) {
                        throw new RuntimeException("Something Went missmatch awb count Wrong");
                }

                // Load JRXML
                InputStream reportStream = new ClassPathResource("reports/invoice.jrxml")
                                .getInputStream();

                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

                Map<String, Object> params = new HashMap<>();

                // ======Title of Report=======
                params.put("proforma", "TAX INVOICE"); // keep hardcode

                // // ================= Cleitn =================
                // params.put("client", client.getClientName());

                params.put("name", client.getClientName());
                params.put("address", client.getClientAdd() + "- " + client.getClientPin());
                params.put("tax_no", client.getClientGstin());
                params.put("pan_no", "PAN No. : " + client.getCompPan());
                params.put("mobile_no", client.getContNo());
                params.put("email", client.getContMail());
                params.put("tag_line", client.getTagLine());
                params.put("comp_state", clientState);
                params.put("comp_statecode", cleintStno);
                params.put("signature", "classpath:static/signature.png"); // stamp or sell path
                params.put("image2", "classpath:static/company/logo.png");

                // ==========bankDeatils=========
                params.put("bankName", "Bank Name : " + client.getBName());
                params.put("accountNo", "Account No. : " + client.getBAcc());
                params.put("IFCCode", "IFSC Code : " + client.getBAcc());
                params.put("saccode", client.getSacCode());
                params.put("uamno", "MSME No. :" + client.getCompMsme());
                params.put("upi", "classpath:static/company/upi.png"); // QR
                params.put("otherRTOReason", "Late delivery penalty");

                // Customer mapping
                params.put("ms", ""); // add space to ident
                params.put("client_nm", customer.getCustName());
                params.put("sub_client_cd", ""); // sub comp name
                params.put("client_address", customer.getCustAdd() + "-" + customer.getCustPin());
                params.put("gstin_no", customer.getCustGst());
                params.put("po_no", ""); // PO no.
                params.put("STA_NM", custState);
                params.put("Place of supply", "Place of supply (State Name & Code): " + custState + " & " + custStno);
                params.put("sezType", ""); // sez ?
                params.put("client_cd", customer.getCustCode()); // client code
                params.put("remarks", " ");
                // params.put("cln_state", "Delhi"); // might be alt of STA_NM
                // params.put("cln_statecode", "07");

                // Invoice details
                params.put("invoice_no", invoice.getInvNo());
                params.put("invoice_dt", java.sql.Date.valueOf(invoice.getInvDate()));
                params.put("from_date", java.sql.Date.valueOf(invoice.getInvDateFrom()));
                params.put("to_date", java.sql.Date.valueOf(invoice.getInvDateTo()));
                params.put("in_words", "Two Thousand Four Hundred Eighty Only");
                params.put("otherRTOReason", ""); // future tax add on
                params.put("dueDate", "");

                BigDecimal frate = invoice.getFuelAmt()
                                .divide(invoice.getInvBaseAmt(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal drate = invoice.getDiscountAmt()
                                .divide(invoice.getInvBaseAmt(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP);

                boolean hasCgst = invoice.getCgstAmt().compareTo(BigDecimal.ZERO) > 0;
                boolean hasIgst = invoice.getIgstAmt().compareTo(BigDecimal.ZERO) > 0;

                // Naming the fields left side
                params.put("fuelTaxName", "Fuel Surcharge @" + frate + " %");
                params.put("serviceTaxName", "CGST @" + (hasCgst ? "9.00 " : "0.00 ") + "%");
                params.put("cessName", "CGST @" + (hasCgst ? "9.00 " : "0.00 ") + "%");
                params.put("HCessName", "IGST @" + (hasIgst ? "18.00 " : "0.00 ") + "%");
                params.put("type", "Discount @ " + drate + " %"); // label can we used for discount

                // ================= NUMERIC (Double) =================
                params.put("value", invoice.getDiscountAmt()); // input can we used for discount
                params.put("rptsubamt", invoice.getInvBaseAmt());
                params.put("fuel", invoice.getFuelAmt());
                params.put("taxableValue",
                                invoice.getFuelAmt().add(invoice.getInvBaseAmt()).subtract(invoice.getDiscountAmt())); // need
                                                                                                                       // at
                                                                                                                       // db
                                                                                                                       // level

                params.put("sTax", invoice.getCgstAmt()); // cgst
                params.put("ECess", invoice.getSgstAmt()); // sgst
                params.put("HECess", invoice.getIgstAmt()); // igst
                params.put("grdTotal", invoice.getInvAmt());
                params.put("in_words", AmountToWordsUtil.convert(invoice.getInvAmt()));

                // =========Extra Fileds no use
                // params.put("netPay", 0.00);
                // params.put("PaybleAmount", 0.00);
                // params.put("payableWithDue", 0.00);
                // params.put("otherRTOAmount", 0.00);

                // Using predefined condition for now
                params.put("condition2", client.getCondition1());
                params.put("condition3",
                                client.getCondition2());
                params.put("condition4", client.getCondition3());
                params.put("condition5",
                                client.getCondition4());
                params.put("condition6", client.getCondition5());

                List<Map<String, Object>> list = new ArrayList<>();

                for (EntryProjection e : eProjection) {
                        Map<String, Object> row1 = new HashMap<>();
                        row1.put("awb_no", e.getAwbNo());
                        row1.put("destination", e.getCenterName());
                        row1.put("service", e.getSrvType());
                        row1.put("pkgs", 1); // need fix ,add db column
                        row1.put("bil_wt", e.getWeight());
                        if (e.getWeight().compareTo(new BigDecimal("0.100")) == 0) {
                                row1.put("type", "DX");
                        } else {
                                row1.put("type", "ND");
                        }
                        row1.put("amount", e.getCharge());
                        row1.put("boking_dt", java.sql.Date.valueOf(e.getAwbDate()));
                        row1.put("state", e.getStateCode()); // need fix, no logic
                        list.add(row1);
                }

                System.out.println(list.size());

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

                return JasperExportManager.exportReportToPdf(jasperPrint);
        }

}
