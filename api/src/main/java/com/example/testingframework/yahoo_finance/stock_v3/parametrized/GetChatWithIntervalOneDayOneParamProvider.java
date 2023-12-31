package com.example.testingframework.yahoo_finance.stock_v3.parametrized;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static com.example.testingframework.yahoo_finance.stock_v3.request.RapidApiClientResolverImpl.customers;

public class GetChatWithIntervalOneDayOneParamProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        String interval = "1d";
        String range = "1mo";

        return customers.stream()
                .map(customer -> Arguments.of(new RequestParams(interval, range, customer)));
    }
}

