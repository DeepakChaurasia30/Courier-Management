package com.courier.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state_tbl")
@Getter
@Setter
public class State {

    @Id
    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Column(name = "state_name", nullable = false, length = 30)
    private String stateName;

    @Column(name = "zone", nullable = false, length = 50)
    private String zone;

    @Column(name = "gst_state_code", nullable = false, length = 50)
    private String gstScode;
}
