package com.example.testingframework.yahoo_finance.stock_v3.request;

import com.example.testingframework.yahoo_finance.stock_v3.model.model.YahooFinanceAPIRequest;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RapidApiClientImpl implements RapidApiClient {

    private final String authToken;

    public RapidApiClientImpl(String key) {
        this.authToken = key;
    }

    @Override
    public Response getChart(YahooFinanceAPIRequest request) {
        return getRequest(authToken, request, Constants.CHART_GET_ENDPOINT);
    }

    private Response getRequest(String key, YahooFinanceAPIRequest request, String basePath) {
        RequestSpecification requestSpec = given()
                .header("X-RapidAPI-Key", key)
                .queryParam("interval", request.getInterval())
                .queryParam("symbol", request.getSymbol())
                .queryParam("range", request.getRange());

        addQueryParamIfNotNull(requestSpec, "region", request.getRegion());
        addQueryParamIfNotNull(requestSpec, "period1", request.getPeriod1());
        addQueryParamIfNotNull(requestSpec, "period2", request.getPeriod2());
        addQueryParamIfNotNull(requestSpec, "comparisons", request.getComparisons());
        addQueryParamIfNotNull(requestSpec, "includePrePost", request.getIncludePrePost());
        addQueryParamIfNotNull(requestSpec, "useYfid", request.getUseYfid());
        addQueryParamIfNotNull(requestSpec, "events", request.getEvents());

        return requestSpec.when().get(String.join("/", basePath));
    }

    private void addQueryParamIfNotNull(RequestSpecification requestSpec, String paramName, Object paramValue) {
        if (paramValue != null) {
            requestSpec.queryParam(paramName, paramValue.toString());
        }
    }
}
