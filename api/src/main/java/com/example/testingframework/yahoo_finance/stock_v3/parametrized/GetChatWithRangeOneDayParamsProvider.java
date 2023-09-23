package com.example.testingframework.yahoo_finance.stock_v3.parametrized;

import com.example.testingframework.yahoo_finance.stock_v3.request.RapidApiClientResolverImpl;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import java.util.List;

public class GetChatWithRangeOneDayParamsProvider implements ArgumentsProvider {

    private static final String interval = "1d";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return RapidApiClientResolverImpl.customers.stream()
                .flatMap(this::createArgumentsForCustomer);
    }

    private Stream<Arguments> createArgumentsForCustomer(String customer) {
        List<String> intervalList = List.of("1mo", "3mo");

        return intervalList.stream()
                .map(intervalValue -> Arguments.of(new RequestParams(interval, intervalValue, customer)));
    }
}
