package com.example.testingframework.yahoo_finance.stock_v3.utils;

// FileUtils.java


import com.example.testingframework.yahoo_finance.stock_v3.model.MarketDataPoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
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
            MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, MarketDataPoints.class);
            return objectMapper.readValue(inputFile, mapType);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static <T> void deleteDataFromJsonFile(String fileName) {
        // Create a Path object for the file
        Path jsonFilePath = Paths.get(fileName);

        // Check if the file exists
        if (!Files.exists(jsonFilePath)) {
            System.out.println("File " + fileName + " does not exist.");
            return;
        }

        // Create an empty data object (for example, an empty Map for JSON)
        T emptyData = (T) new HashMap<String, Object>();

        // Serialize the empty data to JSON and write it back to the file
        writeToJsonFile(emptyData, fileName);

        System.out.println("Data cleared in " + fileName);
    }



    public static <T> void writeToJsonFile(T data, String fileName) {
        try {
            File outputFile = new File(fileName);
            objectMapper.writeValue(outputFile, data);
            System.out.println("Data written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readFromJsonFile(String fileName, Class<T> dataType) throws IOException {
        File jsonFile = new File(fileName);
        if (!jsonFile.exists()) {
            return null; // Return null if the file doesn't exist
        }

        return objectMapper.readValue(jsonFile, dataType);
    }
    public static void clearFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    public static String getPathToFile(String fileName) {
        // Use a relative path based on your project structure
        File file = new File(fileName);
        if (file.isAbsolute()) {
            return fileName;
        }
        String relativePath = "../" + fileName; // Adjust this path as needed

        String absolutePath = null;

        try {
            // Get the canonical file representing the absolute path
            file = new File(relativePath).getCanonicalFile();

            // Get the absolute path as a string
            absolutePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return absolutePath;
    }

    public static void createFileIfNotExists(Path jsonFilePath) throws IOException {
        if (!Files.exists(jsonFilePath)) {
            try {
                Files.createFile(jsonFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Failed to create the file.");
            }
        }
    }





}
