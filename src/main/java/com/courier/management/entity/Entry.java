package com.courier.management.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "entry_tbl", uniqueConstraints = @UniqueConstraint(columnNames = { "awb_no", "client_id" }))
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "awb_no", nullable = false, length = 20)
    private String awbNo;

    @Column(name = "awb_date", nullable = false) // ✅ FIX
    private LocalDate awbDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dest_id", nullable = false)
    private Center center;

    @Column(name = "weight", nullable = false)
    private BigDecimal weight;

    @Column(name = "charge", nullable = false)
    private BigDecimal charge;

    @Column(name = "srv_type", nullable = false)
    private String srvType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inv_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id", nullable = false)
    private Customer customer;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // hybird model for DTO

    @Column(name = "cust_id", nullable = false, insertable = false, updatable = false)
    private Long custid;
    @Column(name = "dest_id", nullable = false, insertable = false, updatable = false)
    private Long destid;
    @Column(name = "client_id", nullable = false, insertable = false, updatable = false)
    private Integer clientid;

    @Column(name = "pincode", nullable = false)
    private String pinCode;

}
