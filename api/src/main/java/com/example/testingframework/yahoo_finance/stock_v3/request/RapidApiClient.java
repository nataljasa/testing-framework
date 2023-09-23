package com.example.testingframework.yahoo_finance.stock_v3.request;


import com.example.testingframework.yahoo_finance.stock_v3.model.model.YahooFinanceAPIRequest;
import io.restassured.response.Response;

public interface RapidApiClient {

    Response getChart(YahooFinanceAPIRequest request);



}
