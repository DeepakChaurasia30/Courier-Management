package com.courier.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice_sequence")
@Getter @Setter @NoArgsConstructor
public class InvoiceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String fy;

    @Column(name = "gst_last")
    private Integer gstLast = 0;

    @Column(name = "ng_last")
    private Integer ngLast = 0;

    @ManyToOne(fetch = FetchType.LAZY)  // or EAGER if you always want client data
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

}