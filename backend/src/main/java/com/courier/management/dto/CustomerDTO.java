package com.courier.management.dto;
    
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {

    // private Long custId;           // optional, for updates
    private String custCode;
    private String custName;
    private String contPerson;
    private String contNo;
    private String custMail;
    private String custGst;
    private String custAdd;
    private String custPin;
    private Boolean isGst;
    private BigDecimal fuelRate;
    private BigDecimal discountRate;
    
    private Integer clientId;       // maps to Customer.client
    private String custStateCode;   // maps to Customer.state.stateCode
}
