package com.courier.management.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class InvoiceDTO {

    private String invNo;
    private LocalDate invDate;
    private LocalDate invDateFrom;
    private LocalDate invDateTo;
    private Long custid;
    private Integer clientid;
    private String discount;
}
