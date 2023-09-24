package com.example.testingframework.yahoo_finance.stock_v3.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MarketDataPoints {

    private List<Double>  open;
    private List<Double>  volume;
    private List<Double>  close;
    private List<Double>  high;
    private List<Double> low;
    private List<Double> adjclose;

    @JsonCreator
    public MarketDataPoints(
                           @JsonProperty("open") List<Double>  open,
                           @JsonProperty("volume") List<Double>  volume,
                           @JsonProperty("close") List<Double>  close,
                           @JsonProperty("high") List<Double>  high,
                           @JsonProperty("low") List<Double>  low,
                           @JsonProperty("adjclose") List<Double> adjclose)  {

        this.open = open;
        this.volume = volume;
        this.close = close;
        this.high = high;
        this.low = low;
        this.adjclose = adjclose;
    }

}
