package com.example.testingframework;

import com.example.testingframework.yahoo_finance.stock_v3.config.Log4jTestWatcher;

import com.example.testingframework.yahoo_finance.stock_v3.request.RapidApiClientResolver;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;
import com.example.testingframework.yahoo_finance.stock_v3.utils.FileUtils;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;


@ExtendWith(SpringExtension.class)
@ExtendWith({Log4jTestWatcher.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = ApiTestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public abstract class ApiTestsConfig {


    public static String baseApiUrl;

    @Value("${qa.api.url}")
    public void setBaseUrl(String url) {
        baseApiUrl = url;
    }


    @Autowired
    protected RapidApiClientResolver requestExecutor;


    public static List<String> customers;

    @Value("#{'${customers}'.split(',')}")
    public void setCustomers(List<String> customer) {
        customers = customer;
    }

    @BeforeAll
    public void setUpAll() {
        RestAssured.baseURI = baseApiUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        FileUtils.deleteDataFromJsonFile(FileUtils.getPathToFile(Constants.MARKET_DATA_INTERVAL_1_DAY_FILE));

    }


}
