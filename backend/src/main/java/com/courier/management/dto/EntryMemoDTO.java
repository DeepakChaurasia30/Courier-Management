package com.courier.management.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class EntryMemoDTO {

    private Long count;
    private BigDecimal totalCharge;


}