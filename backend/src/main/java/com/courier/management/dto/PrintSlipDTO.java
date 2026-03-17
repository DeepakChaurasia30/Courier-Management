package com.courier.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PrintSlipDTO {

    private String awbNo;
    private LocalDate awbDate;
    private String centerDestName;
    private BigDecimal charge;
    private String courierName;
    private String customerCustCode;
    private String noPcs;
    private String pinCode;
    private String remark;
    private String srvType;
    private BigDecimal volWeight;
    private BigDecimal weight;
    private  String ptype;


    
}
