package com.example.testingframework.yahoo_finance.stock_v3.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketDataPoints {

    private double regularMarketPrice;
    private double chartPreviousClose;
    private double previousClose;

    @JsonCreator
    public MarketDataPoints(
                           @JsonProperty("regularMarketPrice") double regularMarketPrice,
                           @JsonProperty("chartPreviousClose") double chartPreviousClose,
                           @JsonProperty("previousClose") double previousClose) {

        this.regularMarketPrice = regularMarketPrice;
        this.chartPreviousClose = chartPreviousClose;
        this.previousClose = previousClose;
    }

}
