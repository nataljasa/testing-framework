package com.example.testingframework.yahoo_finance.stock_v3.parametrized;


import java.util.List;

public record DataToVerify(String key, List<Double>open, List<Double> volume, List<Double> close,
                           List<Double> high, List<Double> low, List<Double> adjclose) {
}
