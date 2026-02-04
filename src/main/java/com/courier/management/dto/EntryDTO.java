package com.courier.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EntryDTO {


    private Long id;
    private String awbNo;
    private LocalDate awbDate;
    private BigDecimal weight;
    private BigDecimal charge;
    private String srvType;
    private LocalDateTime entryDate;

    private Long destid;

    private Long customerId;

    private Long clientId;

    private Long invoiceId; // nullable
     
}
