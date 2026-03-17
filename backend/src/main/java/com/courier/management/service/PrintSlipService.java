package com.courier.management.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.repository.ClientRepository;
import com.courier.management.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.courier.management.dto.PrintSlipDTO;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

@Service
@RequiredArgsConstructor
public class PrintSlipService {

    @Value("G:/Courier/uploads/courier_logo")
    private String logoDir;
        private final ClientRepository clientRepository;
        private final CustomerRepository customerRepository;

        public byte[] generateSlip(List<PrintSlipDTO> dtoList) throws Exception {


                System.out.println(dtoList);
                List<Map<String, ?>> records = new ArrayList<>();

                for (PrintSlipDTO s : dtoList) {

                        Customer customer = customerRepository.findByCustCode(s.getCustomerCustCode());
                        Client client = customer.getClient();
                        Map<String, Object> map = new HashMap<>();

                        map.put("awb_no", s.getAwbNo());
                        map.put("destination_city", s.getCenterDestName());
                        map.put("booking_date", s.getAwbDate());
                    if ("SF".equals(s.getSrvType()))
                        map.put("service_code", "SURFACE ");   // truck style

                    else if ("AR".equals(s.getSrvType()))
                        map.put("service_code", "AIR");           // plane style

                    else if ("FT".equals(s.getSrvType()))
                        map.put("service_code", "PREMIUM");           // premium/express

                        map.put("packets",
                                        s.getNoPcs() == null ? 0 : Integer.parseInt(s.getNoPcs()));
                    BigDecimal actualWeight =
                            s.getWeight() != null ? s.getWeight() : BigDecimal.ZERO;

                    BigDecimal volWeight =
                            s.getVolWeight() != null ? s.getVolWeight() : BigDecimal.ZERO;

                    BigDecimal billingWeight = actualWeight.max(volWeight);

                    map.put("actual_weight", actualWeight);
                    map.put("volume_weight", volWeight);
                    map.put("billing_weight", billingWeight);
                        map.put("freight_amount", s.getCharge());

                        map.put("origin_city", "Ghaziabad-"+client.getClientPin()); //fix add destination

                        map.put("consignor_name", customer.getCustName());
                        map.put("consignor_phone", customer.getContNo().isEmpty()?"":customer.getContNo());
                        map.put("consignor_address", customer.getCustAdd());
                        map.put("consignor_gstin", customer.getIsGst()?customer.getCustGst():"");

                        map.put("consignee_name", s.getRemark());
                        map.put("consignee_phone", " ");
                        map.put("consignee_address",
                                        s.getCenterDestName() + " - " + s.getPinCode());

                        map.put("insurance_value", 0.0);
                        map.put("document_type", s.getPtype().equals("DX")?"DOCUMENT":"PARCEL");
                        map.put("insurance_type", "Ow");

                        map.put("net_name", client.getClientName());
                        map.put("net_address", client.getClientAdd());

                        if(s.getCourierName().equals("SMCS")) {
                                map.put("net_website", "https://shreemaruti.com/");
                                map.put("net_email", "info@shreemaruti.com");
                                map.put("network_logo",logoDir+"/smcs_logo.png");
                        }
                        if(s.getCourierName().equals("DTDC")) {
                                map.put("net_website", "https://www.dtdc.com/in/");
                                map.put("net_email", "customersupport@dtdc.com");
                                map.put("network_logo",logoDir+"/dtdc_logo.png");
                        }
                        if(s.getCourierName().equals("BD")) {
                                map.put("net_website", "https://bluedart.com/");
                                map.put("net_email", "customerservice@bluedart.com");
                                map.put("network_logo",logoDir+"/blue_logo.png");

                        }
                        if(s.getCourierName().equals("TRAC")) {
                                map.put("net_website", "https://www.trackon.in/");
                                map.put("net_email", " customercare@trackon.in");
                                map.put("network_logo",logoDir+"/trac_logo.png");

                        }
                        if(s.getCourierName().equals("SKY")) {
                                map.put("net_website", "https://skyking.co/");
                                map.put("net_email", " support@skyking.co");
                                map.put("network_logo",logoDir+"/sky_logo.png");

                        }
                        if(s.getCourierName().equals("OLS")) {
                                map.put("net_website", "https://www.olslogistics.in/");
                                map.put("net_email", "support@olslogistics.in");
                                map.put("network_logo",logoDir+"/ols_logo.jpg");

                        }
                        if(s.getCourierName().equals("OTHER")) {
                                map.put("net_website", " ");
                                map.put("net_email", "dineshchaurasia084@gmail.com");
                                map.put("network_logo",logoDir+"/blue_logo.png");

                        }

                        map.put("copyType", "CUSTOMER COPY");
                        map.put("total", String.valueOf(s.getCharge()));
//                        map.put("rate_qr",logoDir+"/rate_qr.jpg");

                        records.add(map);
                }

                // Load JRXML
                InputStream reportStream = getClass().getClassLoader()
                                .getResourceAsStream("reports/EmailDocket.jrxml");

                if (reportStream == null) {
                        throw new RuntimeException("JRXML file not found");
                }

                // Compile Report
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

                // Parameters
                Map<String, Object> params = new HashMap<>();

                params.put("field1", "TOTAL");
                params.put("freightFlag", true);
//                params.put("rate_qr",logoDir+"/rate_qr.jpg");

                // Load Logo
                // InputStream logoStream =
                // new ClassPathResource("images/logo.png").getInputStream();

                // params.put("network_logo", "classpath:static/company/logo.png");

                // DataSource
                JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(records);

                // Fill report
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

                // Export PDF
                return JasperExportManager.exportReportToPdf(jasperPrint);
        }
}