package com.example.testingframework.yahoo_finance.stock_v3.utils;

import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NumberUtils {

    public static  List<Double> parseDoubleList(Map<String, Object> indicatorsData, String jsonPath) {
        return Arrays.stream(JsonPath.read(indicatorsData, jsonPath)
                        .toString()
                        .replaceAll("[\\[\\]]", "")
                        .split(","))
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
    }


    public static double roundToTwoDecimalPlaces(Object value) {
        if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            // Round up to two decimal places using RoundingMode.CEILING
            BigDecimal bd = new BigDecimal(doubleValue);
            bd = bd.setScale(2, RoundingMode.CEILING);

            // Check if the rounded value is greater than or equal to 1 million
            if (bd.doubleValue() >= 1000000.0) {
                // Format the value as millions and return it as a double
                double formattedValue = bd.doubleValue() / 1000000.0;
                return formattedValue;
            } else {
                // Return the rounded value as a double
                return bd.doubleValue();
            }
        } else if (value instanceof String) {
            String stringValue = (String) value;
            try {
                // Attempt to parse the value as a BigDecimal
                BigDecimal bd = new BigDecimal(stringValue);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                // Check if the rounded value is greater than or equal to 1 million
                if (bd.doubleValue() >= 1000000.0) {
                    // Format the value as millions and return it as a double
                    double formattedValue = bd.doubleValue() / 1000000.0;
                    return formattedValue;
                } else {
                    // Return the rounded value as a double
                    return bd.doubleValue();
                }
            } catch (NumberFormatException e) {
                // Handle invalid input gracefully, returning 0.00 as a default double.
                return 0.00;
            }
        }
        // Handle other types or invalid input gracefully, returning 0.00 as a default double.
        return 0.00;
    }

}
