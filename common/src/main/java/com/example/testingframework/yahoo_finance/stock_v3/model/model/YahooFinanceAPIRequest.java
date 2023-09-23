package com.example.testingframework.yahoo_finance.stock_v3.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class YahooFinanceAPIRequest {
    private String interval;
    private String symbol;
    private String range;
    private Number region;
    private Number period1;
    private Number period2;
    private String comparisons;
    private Boolean includePrePost;
    private Boolean useYfid;
    private Boolean includeAdjustedClose;
    private String events;




}
