package com.courier.management.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EntryProjection {

    String getAwbNo();

    LocalDate getAwbDate();

    String getCenterName();

    BigDecimal getWeight();

    BigDecimal getCharge();

    String getSrvType();

    String getStateCode();


}
