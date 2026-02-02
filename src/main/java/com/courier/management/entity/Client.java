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

@Entity
@Table(name = "client_tbl",
       uniqueConstraints = @UniqueConstraint(columnNames = "client_name"))
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clientId;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientAdd;

    @Column(nullable = false)
    private Long clientPin;

    @Column(nullable = false)
    private String clientGstin;

    @Column(nullable = false)
    private Boolean clientIsgst;

    @Column(nullable = false)
    private LocalDateTime clientCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_state_code", nullable = false)
    private State state;

    @Column(nullable = false)
    private BigDecimal taxRate;
}

