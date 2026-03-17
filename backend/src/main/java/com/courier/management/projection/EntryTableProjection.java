package com.courier.management.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EntryTableProjection {

    String getAwbNo();

    LocalDate getAwbDate();

    String getPinCode();

    BigDecimal getWeight();

    BigDecimal getCharge();

    String getSrvType();

    String getCustomerCustCode();

    String getPtype();

    String getCenterDestName();

    String getNoPcs();

    String getVolWeight();

    String getCourierName();

    String getRemark();
}
