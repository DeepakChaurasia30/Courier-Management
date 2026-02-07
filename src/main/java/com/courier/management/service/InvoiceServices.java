package com.courier.management.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.courier.management.dto.InvoiceDTO;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.Entry;
import com.courier.management.entity.Invoice;
import com.courier.management.enums.AnyStatus;
import com.courier.management.helper.InvoiceHelper;
import com.courier.management.repository.ClientRepository;
import com.courier.management.repository.CustomerRepository;
import com.courier.management.repository.EntryRepository;
import com.courier.management.repository.InvoiceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServices {

        private final EntryRepository entryRepository;
        private final InvoiceRepository invoiceRepository;
        private final InvoiceHelper invoiceHelper;
        private final CustomerRepository customerRepository;
        private final ClientRepository clientRepository;

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

                // ---------- Fuel & Discount (from cust_tbl) ----------
                BigDecimal fuelRate = customer.getFuelRate(); // e.g. 10.00
                BigDecimal discountRate = customer.getDiscountRate(); // e.g. 5.00

                BigDecimal fuelAmt = baseAmount
                                .multiply(fuelRate)
                                .divide(BigDecimal.valueOf(100));

                BigDecimal discountAmt = baseAmount
                                .multiply(discountRate)
                                .divide(BigDecimal.valueOf(100));

                BigDecimal taxableAmt = baseAmount
                                .add(fuelAmt)
                                .subtract(discountAmt);

                // ---------- GST ----------
                BigDecimal cgst = BigDecimal.ZERO;
                BigDecimal sgst = BigDecimal.ZERO;
                BigDecimal igst = BigDecimal.ZERO;

                if (customer.getIsGst()) {

                        BigDecimal gstRate = client.getTaxRate(); // standard GST %

                        if (!customer.getState().equals(client.getState())) {
                                igst = taxableAmt.multiply(gstRate).divide(BigDecimal.valueOf(100));
                        } else {
                                BigDecimal halfGst = gstRate.divide(BigDecimal.valueOf(2));
                                cgst = taxableAmt.multiply(halfGst).divide(BigDecimal.valueOf(100));
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

                Invoice savedInvoice = invoiceRepository.save(invoice);

                // ---------- Update Entries ----------
                entries.forEach(e -> e.setInvoice(savedInvoice));
                entryRepository.saveAll(entries);
        }

        public AnyStatus isInvStatus(String inv_no) {
                String inv = inv_no.trim().toUpperCase(); // be safer side

                Optional<AnyStatus> optentry = invoiceRepository.findByInvNoS(inv);

                if (optentry.isEmpty()) {
                        return AnyStatus.NEW;
                }

                return AnyStatus.NOT_UPDATABLE;
        }

        public void deleteInv(String inv_no)
         {

                String inv = inv_no.trim().toUpperCase(); // be safer side

                Invoice invoice = invoiceRepository.findByInvNo(inv)
                                .orElseThrow(() -> new RuntimeException("Invoice Couldn't found, Try Printing"));
                
                List<Entry> entrylist = entryRepository.findByInvoice(invoice);

                  // ---------- Update Entries ---------

                if(entrylist.isEmpty())
                {
                        throw new RuntimeException("Something Went wrong");
                }


                entrylist.forEach(e -> e.setInvoice(null));
                entryRepository.saveAll(entrylist);
                invoiceRepository.deleteById(invoice.getInvId());

                // above all run or none
                

        }

}
