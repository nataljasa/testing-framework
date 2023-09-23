
package com.example.testingframework.yahoo_finance.stock_v3.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class RapidApiClientResolverImpl implements RapidApiClientResolver {

    @Value("${api_key.customerOne.valid.request}")
    public String customerOneValidApiKey;

    @Value("${api_key.customerOne.unauthorized.request}")
    public String customerOneUnAuthorizedApiKey;

    @Value("${api_key.customerOne.expired.request}")
    public String customerOneUnExpiredApiKey;

    public static List<String> customers;

    @Value("#{'${customers}'.split(',')}")
    public void setCustomers(List<String> customer) {
        customers = customer;
    }

    private final Map<String, String> mapValidRequest = new HashMap<>();
    private final Map<String, String> mapExpiredRequest = new HashMap<>();
    private final Map<String, String> mapUnauthorizedRequest = new HashMap<>();

    @PostConstruct
    private void init() {
        updateMap(customerOneValidApiKey, mapValidRequest);
        updateMap(customerOneUnExpiredApiKey, mapExpiredRequest);
        updateMap(customerOneUnAuthorizedApiKey, mapUnauthorizedRequest);
    }

    private void updateMap(String apiKey, Map<String, String> map) {
        customers.forEach(customer -> map.put(customer, apiKey));
    }

    @Override
    public RapidApiClient forValidChartKey(String customerId) {
        String authToken = mapValidRequest.get(customerId);
        return new RapidApiClientImpl(authToken);
    }

    @Override
    public RapidApiClient forUnauthorizedChartKey(String customerId) {
        String authToken = mapUnauthorizedRequest.get(customerId);
        return new RapidApiClientImpl(authToken);
    }

    @Override
    public RapidApiClient forExpiredChartKey(String customerId) {
        String authToken = mapExpiredRequest.get(customerId);
        return new RapidApiClientImpl(authToken);
    }
}
