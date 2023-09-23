package com.example.testingframework.yahoo_finance.stock_v3.utils;

// FileUtils.java


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import com.example.testingframework.yahoo_finance.stock_v3.model.model.MarketDataPoints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.openqa.selenium.JavascriptExecutor;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

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

    public static void writeToJsonFile(Map<String, MarketDataPoints> marketData, String fileName) {
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

    public static String getPathToFile(String fileName) {
        String relativePath = "../"+fileName;
        String absolutePath = null;

        try {
            // Get the canonical file representing the absolute path
            File file = new File(relativePath).getCanonicalFile();

            // Get the absolute path as a string
             absolutePath = file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return absolutePath ;
    }




}
