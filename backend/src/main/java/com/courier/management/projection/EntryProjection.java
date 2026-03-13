package com.courier.management.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EntryProjection {

    String getAwbNo();

    LocalDate getAwbDate();

    String getCenterName();

    String getPinCode();

    BigDecimal getWeight();

    BigDecimal getCharge();

    String getSrvType();

    String getStateCode();

    Integer getNoPcs();

    String getPType();

}
