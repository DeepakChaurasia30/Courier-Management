package com.courier.management.projection;

import java.math.BigDecimal;

public interface InvProjection {

    Long getAwbCount();

    Boolean getIsCancel();

    BigDecimal getInvAmt();

}
