package com.courier.management.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "client_tbl", uniqueConstraints = @UniqueConstraint(columnNames = "client_name"))
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_add", nullable = false)
    private String clientAdd;

    @Column(name = "client_pin", nullable = false)
    private Long clientPin;

    @Column(name = "client_gstin", nullable = false)
    private String clientGstin;

    @Column(name = "client_isgst", nullable = false)
    private Boolean clientIsgst;

    @Column(name = "client_create", nullable = false)
    private LocalDateTime clientCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_state_code", nullable = false)
    private State state;

    @Column(name = "tax_rate", nullable = false)
    private BigDecimal taxRate;

    // -------- NEW FIELDS --------

    @Column(name = "tag_line")
    private String tagLine;

    @Column(name = "sac_code")
    private String sacCode;

    @Column(name = "cont_no")
    private String contNo;

    @Column(name = "cont_mail")
    private String contMail;

    @Column(name = "b_name")
    private String bName;

    @Column(name = "b_acc")
    private String bAcc;

    @Column(name = "b_ifsc")
    private String bIfsc;

    @Column(name = "comp_pan")
    private String compPan;

    @Column(name = "comp_msme")
    private String compMsme;

    @Column(name = "condition1", columnDefinition = "TEXT")
    private String condition1;

    @Column(name = "condition2", columnDefinition = "TEXT")
    private String condition2;

    @Column(name = "condition3", columnDefinition = "TEXT")
    private String condition3;

    @Column(name = "condition4", columnDefinition = "TEXT")
    private String condition4;

    @Column(name = "condition5", columnDefinition = "TEXT")
    private String condition5;
}