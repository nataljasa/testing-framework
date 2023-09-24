package com.example.testingframework.yahoo_finance.stock_v3.utils.tags;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag("apiTests")
@ParameterizedTest
public @interface ApiTests {
}
