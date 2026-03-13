package com.courier.management.util;

public final class NumberToWordUtil {

    private static final String[] UNITS = {
            "", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty",
            "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private NumberToWordUtil() {}

    public static String convert(long number) {

        if (number == 0)
            return "Zero";

        if (number < 0)
            return "Minus " + convert(Math.abs(number));

        return convertRec(number).trim();
    }

    private static String convertRec(long num) {

        if (num < 20)
            return UNITS[(int) num];

        if (num < 100)
            return TENS[(int) (num / 10)] + " " + UNITS[(int) (num % 10)];

        if (num < 1_000)
            return UNITS[(int) (num / 100)] + " Hundred " + convertRec(num % 100);

        if (num < 1_00_000)
            return convertRec(num / 1_000) + " Thousand " + convertRec(num % 1_000);

        if (num < 1_00_00_000)
            return convertRec(num / 1_00_000) + " Lakh " + convertRec(num % 1_00_000);

        // CRORE SUPPORT
        return convertRec(num / 1_00_00_000) + " Crore " +
               convertRec(num % 1_00_00_000);
    }
}

