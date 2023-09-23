package com.example.testingframework.yahoo_finance.stock_v3.parametrized;

import com.example.testingframework.yahoo_finance.stock_v3.model.model.MarketDataPoints;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;


import com.example.testingframework.yahoo_finance.stock_v3.utils.FileUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Map;
import java.util.stream.Stream;

import java.io.IOException;



public class JsonFileArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
        final String pathToFile = FileUtils.getPathToFile(Constants.JSON_FILE_PATH);
        Map<String, MarketDataPoints> marketData = FileUtils.readFromJsonFile(pathToFile);

        // Convert MarketDataPoints to RequestParams1 records
        Stream<Arguments> argumentsStream = marketData.entrySet().stream()
                .map(entry -> {
                    MarketDataPoints marketDataPoints = entry.getValue();
                    RequestParams1 requestParams = new RequestParams1(
                            entry.getKey(),
                            marketDataPoints.getOpen(),
                            marketDataPoints.getVolume(),
                            marketDataPoints.getClose(),
                            marketDataPoints.getHigh(),
                            marketDataPoints.getLow(),
                            marketDataPoints.getAdjclose()
                    );
                    return Arguments.of(requestParams);
                });


        return argumentsStream;
    }
}
