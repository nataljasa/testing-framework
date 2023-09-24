package com.example.testingframework.yahoo_finance.stock_v3.utils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.*;
import java.util.regex.*;

public class ChartDataProcessor {
    public static Map<String, Double> processChartData(List<String> chartDataList) {
        Pattern openPattern = Pattern.compile("Open(\\d+\\.\\d+)");
        Pattern highPattern = Pattern.compile("High(\\d+\\.\\d+)");
        Pattern lowPattern = Pattern.compile("Low(\\d+\\.\\d+)");
        Pattern closePattern = Pattern.compile("Close(\\d+\\.\\d+)");

        Map<String, Double> chartDataMap = new HashMap<>();

        for (String data : chartDataList) {
            processValue(data, openPattern, "Open", chartDataMap);
            processValue(data, highPattern, "High", chartDataMap);
            processValue(data, lowPattern, "Low", chartDataMap);
            processValue(data, closePattern, "Close", chartDataMap);
        }

        return chartDataMap;
    }

    private static void processValue(String data, Pattern pattern, String label, Map<String, Double> chartDataMap) {
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            String value = matcher.group(1);
            double numericValue = Double.parseDouble(value);
            System.out.println(label + ": " + value);
            chartDataMap.put(label, numericValue);
        }
    }
}
