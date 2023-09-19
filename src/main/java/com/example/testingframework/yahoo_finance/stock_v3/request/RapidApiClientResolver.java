package com.example.testingframework.yahoo_finance.stock_v3.request;

public interface RapidApiClientResolver {

    RapidApiClient forValidChartKey(String customerId);

    RapidApiClient forUnauthorizedChartKey(String customerId);

    RapidApiClient forExpiredChartKey(String customerId);

}
