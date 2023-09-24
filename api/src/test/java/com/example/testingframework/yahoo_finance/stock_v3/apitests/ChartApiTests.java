package com.example.testingframework.yahoo_finance.stock_v3.apitests;

import com.example.testingframework.ApiTestsConfig;
import com.example.testingframework.yahoo_finance.stock_v3.config.Log4jTestWatcher;

import com.example.testingframework.yahoo_finance.stock_v3.model.MarketDataPoints;
import com.example.testingframework.yahoo_finance.stock_v3.model.YahooFinanceAPIRequest;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithIntervalOneDayOneParamProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithIntervalOneDayParamsProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.RequestParams;

import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;
import com.example.testingframework.yahoo_finance.stock_v3.utils.FileUtils;
import com.example.testingframework.yahoo_finance.stock_v3.utils.tags.ApiTests;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.example.testingframework.yahoo_finance.stock_v3.utils.FileUtils.createFileIfNotExists;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.NumberUtils.parseDoubleList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(Log4jTestWatcher.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ApiTests
public class ChartApiTests extends ApiTestsConfig {

    private static final Logger LOG = Logger.getLogger(ChartApiTests.class);

    @Order(1)
    @ParameterizedTest(name = "Search with expired key - authentication error {0}")
    @ArgumentsSource(GetChatWithIntervalOneDayOneParamProvider.class)
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
    @ArgumentsSource(GetChatWithIntervalOneDayOneParamProvider.class)
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
    @ParameterizedTest(name = "Search with interval 1 day - response 200 {0}")
    @ArgumentsSource(GetChatWithIntervalOneDayParamsProvider.class)
    public void searchWithIntervalOneDay_checkResult_expect200(RequestParams params) throws IOException {
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

        handleMarketDataUpdate(indicatorsData, metaData);
    }


    private void handleMarketDataUpdate(Map<String, Object> indicatorsData, Map<String, Object> metaData) throws IOException {
        Path jsonFilePath = Paths.get(FileUtils.getPathToFile(Constants.MARKET_DATA_INTERVAL_1_DAY_FILE));

        createFileIfNotExists(jsonFilePath);

        Map<String, MarketDataPoints> marketData = readMarketDataFromFile(jsonFilePath);

        String marketDataKey = getMarketDataKey(metaData);

        MarketDataPoints marketDataPoints = createMarketDataPoints(indicatorsData);

        updateMarketData(marketData, marketDataKey, marketDataPoints);

        writeMarketDataToFile(marketData, jsonFilePath);
    }


    private Map<String, MarketDataPoints> readMarketDataFromFile(Path jsonFilePath) throws IOException {
        if (Files.size(jsonFilePath) > 0) {
            return FileUtils.readFromJsonFile(FileUtils.getPathToFile(jsonFilePath.toString()));
        }
        return new HashMap<>();
    }

    private String getMarketDataKey(Map<String, Object> metaData) {
        return JsonPath.read(metaData, "$.range") + "_" + JsonPath.read(metaData, "$.dataGranularity");
    }

    private MarketDataPoints createMarketDataPoints(Map<String, Object> indicatorsData) {
        List<Double> adjcloseValues = parseDoubleList(indicatorsData, "$.adjclose[0].adjclose");
        List<Double> openv = parseDoubleList(indicatorsData, "$.quote[0].open");
        List<Double> volumev = parseDoubleList(indicatorsData, "$.quote[0].volume");
        List<Double> closev = parseDoubleList(indicatorsData, "$.quote[0].close");
        List<Double> highv = parseDoubleList(indicatorsData, "$.quote[0].high");
        List<Double> lowv = parseDoubleList(indicatorsData, "$.quote[0].low");

        return MarketDataPoints.builder()
                .open(openv)
                .volume(volumev)
                .close(closev)
                .high(highv)
                .low(lowv)
                .adjclose(adjcloseValues)
                .build();
    }

    private void updateMarketData(Map<String, MarketDataPoints> marketData, String marketDataKey, MarketDataPoints marketDataPoints) {
        marketData.put(marketDataKey, marketDataPoints);
    }

    private void writeMarketDataToFile(Map<String, MarketDataPoints> marketData, Path jsonFilePath) throws IOException {
        FileUtils.writeToJsonFile(marketData, jsonFilePath.toString());
    }




}
