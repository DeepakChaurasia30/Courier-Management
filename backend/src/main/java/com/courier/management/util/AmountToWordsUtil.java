package com.courier.management.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountToWordsUtil {

    private AmountToWordsUtil() {}

    public static String convert(BigDecimal amount) {

        if (amount == null)
            return "Zero Rupees Only";

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        long rupees = amount.longValue();
        int paise = amount
                .subtract(BigDecimal.valueOf(rupees))
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        StringBuilder words = new StringBuilder();

        if (rupees > 0) {
            words.append(NumberToWordUtil.convert(rupees))
                 .append(" Rupees");
        }

        if (paise > 0) {
            if (rupees > 0) words.append(" and ");
            words.append(NumberToWordUtil.convert(paise))
                 .append(" Paise");
        }

        words.append(" Only");

        return words.toString();
    }
}

