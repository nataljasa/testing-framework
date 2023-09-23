package com.example.testingframework.yahoo_finance.stock_v3.apitests;

import com.example.testingframework.ApiTestsConfig;
import com.example.testingframework.yahoo_finance.stock_v3.config.Log4jTestWatcher;

import com.example.testingframework.yahoo_finance.stock_v3.model.model.MarketDataPoints;
import com.example.testingframework.yahoo_finance.stock_v3.model.model.YahooFinanceAPIRequest;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithRangeOneDayOneParamProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithRangeOneDayParamsProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.RequestParams;

import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;
import com.example.testingframework.yahoo_finance.stock_v3.utils.FileUtils;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(Log4jTestWatcher.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChartApiTests extends ApiTestsConfig {

    private static final Logger LOG = Logger.getLogger(ChartApiTests.class);

    @Order(1)
    @ParameterizedTest(name = "Search with expired key - authentication error {0}")
    @ArgumentsSource(GetChatWithRangeOneDayOneParamProvider.class)
    @Execution(ExecutionMode.CONCURRENT)
    public void searchWithExpiredKey_checkResult_expectAuthenticationError(RequestParams params) {
        //given
        //when
        Response chartResponse = requestExecutor.forExpiredChartKey(params.customer()).getChart(YahooFinanceAPIRequest.builder()
                .interval(params.interval())
                .range(params.range())
                .symbol(Constants.CHART_SYMBOL).build());
        //then
        assertEquals(HttpStatus.SC_UNAUTHORIZED, chartResponse.getStatusCode(), "Wrong status code" + chartResponse.getStatusCode());
        assertEquals("Invalid API key. Go to https://docs.rapidapi.com/docs/keys for more info.", chartResponse.jsonPath().get("message"), "Wrong error message");
    }

    @Order(2)
    @ParameterizedTest(name = "Search with unauthorized key - authorization error {0}")
    @ArgumentsSource(GetChatWithRangeOneDayOneParamProvider.class)
    @Execution(ExecutionMode.CONCURRENT)
    public void searchWithUnauthorizedKey_checkResult_expectAuthorizationError(RequestParams params) {
        //given
        //when
        Response chartResponse = requestExecutor.forUnauthorizedChartKey(params.customer()).getChart(YahooFinanceAPIRequest.builder()
                .interval(params.interval())
                .range(params.range())
                .symbol(Constants.CHART_SYMBOL).build());
        //then
        assertEquals(HttpStatus.SC_FORBIDDEN, chartResponse.getStatusCode(), "Wrong status code" + chartResponse.getStatusCode());
        assertEquals("You are not subscribed to this API.", chartResponse.jsonPath().get("message"), "Wrong error message");
    }

    @Order(3)
    @ParameterizedTest(name = "Search with interval 1 minute - response 200 {0}")
    @ArgumentsSource(GetChatWithRangeOneDayParamsProvider.class)
    @Execution(ExecutionMode.CONCURRENT)
    public void searchWithIntervalOneMinute_checkResult_expect200(RequestParams params) throws IOException {
        // Given
        String customer = params.customer();
        String interval = params.interval();
        String range = params.range();
        String symbol = Constants.CHART_SYMBOL;

        // When
        Response chartResponse = requestExecutor.forValidChartKey(customer)
                .getChart(YahooFinanceAPIRequest.builder()
                        .interval(interval)
                        .range(range)
                        .symbol(symbol)
                        .build());

        // Then
        assertEquals(HttpStatus.SC_OK, chartResponse.getStatusCode(), "Wrong status code" + chartResponse.getStatusCode());

        Map<String, Object> indicatorsData = JsonPath.read(chartResponse.asPrettyString(), "$.chart.result[0].indicators");
        Map<String, Object> metaData = JsonPath.read(chartResponse.asPrettyString(), "$.chart.result[0].meta");
        String resultRange = JsonPath.read(metaData, "$.range");
        String resultDataGranularity = JsonPath.read(metaData, "$.dataGranularity");

        assertEquals(range, resultRange, "Wrong error code");
        assertEquals(interval, resultDataGranularity, "Wrong error code");

        // Handle data reading and writing
        handleMarketDataUpdate(indicatorsData, metaData);
    }


// ...

    private void handleMarketDataUpdate(Map<String, Object> indicatorsData, Map<String, Object> metaData) throws IOException {
        // Define the path to the JSON file
        String fileName = Constants.JSON_FILE_PATH;

        // Create a Path object for the file
        Path jsonFilePath = Paths.get(FileUtils.getPathToFile(fileName));
        // Check if the file exists, and if not, create it
        if (!Files.exists(jsonFilePath)) {
            try {
                Files.createFile(jsonFilePath);
            } catch (IOException e) {
                // Handle any potential file creation errors here
                e.printStackTrace();
                return; // Exit the method if file creation fails
            }
        }
        Map<String, MarketDataPoints> marketData = new HashMap<>();
        if (Files.size(jsonFilePath) > 0) {
            // Read existing data from the JSON file
            marketData = FileUtils.readFromJsonFile(FileUtils.getPathToFile(fileName));
        }

        // Update the marketData with new data
        String marketDataKey = JsonPath.read(metaData, "$.range") + "_" + JsonPath.read(metaData, "$.dataGranularity");
        // Assuming you have a list of Double values for adjclose

        List<Double> adjcloseValues = Arrays.asList(JsonPath.read(indicatorsData, "$.adjclose[0].adjclose")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
        List<Double> openv = Arrays.asList(JsonPath.read(indicatorsData, "$.quote[0].open")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
        List<Double> volumev = Arrays.asList(JsonPath.read(indicatorsData, "$.quote[0].volume")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
        List<Double> closev= Arrays.asList(JsonPath.read(indicatorsData, "$.quote[0].close")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
        List<Double> highv = Arrays.asList(JsonPath.read(indicatorsData, "$.quote[0].high")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());
        List<Double> lowv = Arrays.asList(JsonPath.read(indicatorsData, "$.quote[0].low")
                        .toString().replaceAll("[\\[\\]]", "").split(",")).stream()
                .map(value -> roundToTwoDecimalPlaces(value.toString()))
                .collect(Collectors.toList());

        MarketDataPoints marketDataPoints = MarketDataPoints.builder()
                .open(openv)
                .volume(volumev)
                .close(closev)
                .high(highv)
                .low(lowv)
                .adjclose(adjcloseValues)
                .build();

        marketData.put(marketDataKey, marketDataPoints);

        // Serialize the updated data to JSON and write it back to the file
        FileUtils.writeToJsonFile(marketData, jsonFilePath.toString());
    }


    private double roundToTwoDecimalPlaces(Object value) {
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
