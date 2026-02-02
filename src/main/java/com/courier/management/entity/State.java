package com.courier.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "state_tbl")
public class State {

    @Id
    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Column(name = "state_name", nullable = false, length = 30)
    private String stateName;
}
