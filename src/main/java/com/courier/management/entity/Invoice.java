package com.courier.management.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_tbl")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inv_id")
    private Long invId;

    @Column(name = "inv_date", nullable = false)
    private LocalDate invDate;

    @Column(name = "inv_date_from", nullable = false)
    private LocalDate invDateFrom;

    @Column(name = "inv_date_to", nullable = false)
    private LocalDate invDateTo;

    @Column(name = "inv_base_amt", nullable = false)
    private BigDecimal invBaseAmt;

    @Column(name = "cgst_amt", nullable = false)
    private BigDecimal cgstAmt;

    @Column(name = "sgst_amt", nullable = false)
    private BigDecimal sgstAmt;

    @Column(name = "igst_amt", nullable = false)
    private BigDecimal igstAmt;

    @Column(name = "fuel_amt", nullable = false)
    private BigDecimal fuelAmt;

    @Column(name = "discount_amt", nullable = false)
    private BigDecimal discountAmt;

    @Column(name = "inv_amt", nullable = false)
    private BigDecimal invAmt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
