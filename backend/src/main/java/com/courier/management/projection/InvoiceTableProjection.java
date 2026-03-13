package com.courier.management.projection;

import java.time.LocalDate;

public interface InvoiceTableProjection {

    String getInvNo();

    LocalDate getInvDate();

    LocalDate getInvDateFrom();

    LocalDate getInvDateTo();

    String getCustomerCustCode();

    Boolean getIsCancel();
}
