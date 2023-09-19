
package com.example.testingframework.yahoo_finance.stock_v3.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;


public class Log4jTestWatcher implements TestWatcher {

    private final Logger logger;

    public Log4jTestWatcher() {
        this(LogManager.getLogger(Log4jTestWatcher.class));
    }

    public Log4jTestWatcher(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logger.error("Failed: " + context.getDisplayName(), cause.getCause());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("Success: " + context.getDisplayName());
    }
}

