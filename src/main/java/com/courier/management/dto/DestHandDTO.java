package com.courier.management.dto;

import lombok.Data;

@Data
public class DestHandDTO {

    private String pinCode;
    private Long destId;
    private String centerName;
    private String stateCode;
    private String stateName;
    private String zone;

}
