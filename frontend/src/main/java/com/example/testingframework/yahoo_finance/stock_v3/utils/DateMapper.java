package com.example.testingframework.yahoo_finance.stock_v3.utils;

import java.util.Map;

public class DateMapper {

    public static final Map<String, String> rangeTimeUnitsMapper = Map.of(
            "1mo", "1M",
            "3mo", "3M"
            // Add more mappings as needed
    );
    public static final Map<String, String> intervalTimeUnitsMapper = Map.of(
            "1d", "1 day"
            // Add more mappings as needed
    );


}
