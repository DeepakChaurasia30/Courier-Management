package com.courier.management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDTO {

    private Integer clientId;

    private String clientName;

    private String clientAdd;

    private Long clientPin;

    private String clientGstin;

    private Boolean clientIsgst;

    private LocalDateTime clientCreate;

    private String stateId;

    private BigDecimal taxRate;

    // -------- NEW FIELDS --------

    private String tagLine;

    private String sacCode;

    private String contNo;

    private String contMail;

    private String bName;

    private String bAcc;

    private String bIfsc;

    private String compPan;

    private String compMsme;

    private String condition1;

    private String condition2;

    private String condition3;

    private String condition4;

    private String condition5;
}