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
@Getter @Setter

@Table(
    name = "cust_tbl",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "cust_name"),
        @UniqueConstraint(columnNames = "cust_code"),
        @UniqueConstraint(columnNames = {"cust_gst", "client_id"})
    }
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "cust_name", nullable = false)
    private String custName;

    @Column(name = "cust_code", nullable = false)
    private String custCode;

    @Column(name = "cust_gst", nullable = false)
    private String custGst;

    @Column(name = "cust_add", nullable = false)   // ✅ FIX
    private String custAdd;

    @Column(name = "cust_pin", nullable = false)
    private String custPin;

    @Column(name = "cust_create", nullable = false)
    private LocalDateTime custCreate;

    @Column(name = "is_gst", nullable = false)
    private Boolean isGst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_state_code", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "discount_rate", nullable = false)
    private BigDecimal discountRate;

    @Column(name = "fuel_rate", nullable = false)
    private BigDecimal fuelRate;
}

