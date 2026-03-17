package com.courier.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users_tbl",
        uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}