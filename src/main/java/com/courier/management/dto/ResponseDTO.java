package com.courier.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.courier.management.projection.EntryProjection;

import lombok.Data;

@Data
public class ResponseDTO {

    // Invoice Part
    private String invNo;
    private LocalDate invDate;
    private LocalDate invDateFrom;
    private LocalDate invDateTo;

    private BigDecimal cgstAmt;
    private BigDecimal sgstAmt;
    private BigDecimal igstAmt;
    private BigDecimal fuelAmt;
    private BigDecimal discountAmt;
    private BigDecimal invBaseAmt;
    private BigDecimal invAmt;
    private String amtWord;

    // Customer Part
    private String custName;
    private String custGst;
    private String custAdd;
    private String custstateName;
    private String custPin;
    private Boolean isGst;
    private BigDecimal discountRate;
    private BigDecimal fuelRate;

    // Client (Owner)
    private String clientName;
    private String clientAdd;
    private Long clientPin;
    private String clientGstin;
    // private Boolean clientIsgst;
    private String clientstateName;
    // private BigDecimal taxRate;
    
    // Entry field 
    List<EntryProjection> entryList;
    
}
