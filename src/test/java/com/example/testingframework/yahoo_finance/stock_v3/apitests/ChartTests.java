package com.example.testingframework.yahoo_finance.stock_v3.apitests;

import com.example.testingframework.ApiTestsConfig;
import com.example.testingframework.yahoo_finance.stock_v3.config.Log4jTestWatcher;
import com.example.testingframework.yahoo_finance.stock_v3.model.MarketDataPoints;
import com.example.testingframework.yahoo_finance.stock_v3.model.YahooFinanceAPIRequest;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithIntervalOneMinuteOneParamProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.GetChatWithIntervalOneMinuteParamsProvider;
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

import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(Log4jTestWatcher.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChartTests extends ApiTestsConfig {

    private static final Logger LOG = Logger.getLogger(ChartTests.class);

    @Order(1)
    @ParameterizedTest(name = "Search with expired key - authentication error {0}")
    @ArgumentsSource(GetChatWithIntervalOneMinuteOneParamProvider.class)
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
    @ArgumentsSource(GetChatWithIntervalOneMinuteOneParamProvider.class)
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
    @ArgumentsSource(GetChatWithIntervalOneMinuteParamsProvider.class)
    @Execution(ExecutionMode.CONCURRENT)
    public void searchWithIntervalOneMinute_checkResult_expect200(RequestParams params) {
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

        Map<String, Object> metaData = JsonPath.read(chartResponse.asPrettyString(), "$.chart.result[0].meta");
        String resultRange = JsonPath.read(metaData, "$.range");
        String resultDataGranularity = JsonPath.read(metaData, "$.dataGranularity");

        assertEquals(range, resultRange, "Wrong error code");
        assertEquals(interval, resultDataGranularity, "Wrong error code");

        // Handle data reading and writing
        handleMarketDataUpdate(metaData);
    }


    private void handleMarketDataUpdate(Map<String, Object> metaData) {
        // Read existing data from the JSON file
        Map<String, MarketDataPoints> marketData = FileUtils.readFromJsonFile(Constants.JSON_FILE_PATH);

        // Update the marketData with new data
        String marketDataKey = JsonPath.read(metaData, "$.range") + "_" + JsonPath.read(metaData, "$.dataGranularity");
        MarketDataPoints marketDataPoints = MarketDataPoints.builder()
                .regularMarketPrice(Double.parseDouble(JsonPath.read(metaData, "$.regularMarketPrice").toString()))
                .chartPreviousClose(Double.parseDouble(JsonPath.read(metaData, "$.chartPreviousClose").toString()))
                .previousClose(Double.parseDouble(JsonPath.read(metaData, "$.previousClose").toString()))
                .build();

        marketData.put(marketDataKey, marketDataPoints);

        // Serialize the updated data to JSON and write it back to the file
       FileUtils.writeToJsonFile(marketData,Constants.JSON_FILE_PATH);
    }

}
