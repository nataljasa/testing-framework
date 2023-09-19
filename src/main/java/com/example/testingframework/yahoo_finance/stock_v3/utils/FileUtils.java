package com.example.testingframework.yahoo_finance.stock_v3.utils;

// FileUtils.java
import com.example.testingframework.yahoo_finance.stock_v3.model.MarketDataPoints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, MarketDataPoints> readFromJsonFile(String fileName) {
        File inputFile = new File(fileName);

        if (!inputFile.exists()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(inputFile, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void writeToJsonFile(Map<String, MarketDataPoints> marketData,String fileName) {
        try {
            File outputFile = new File(fileName);
            objectMapper.writeValue(outputFile, marketData);
            System.out.println("Data written to market_data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clearFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }
}
