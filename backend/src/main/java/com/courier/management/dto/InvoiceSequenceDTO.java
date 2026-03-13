package com.courier.management.dto;

import lombok.Data;

@Data
public class InvoiceSequenceDTO {

    private Integer id;
    private String fy;
    private Integer gstLast;
    private Integer ngLast;

}