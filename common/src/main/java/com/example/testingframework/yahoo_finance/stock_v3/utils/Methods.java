
package com.example.testingframework.yahoo_finance.stock_v3.utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class Methods {

    public static void openBrowserIfNeeded() {
        if (getWebDriver().toString().contains("null")) {
            open(RestAssured.baseURI);
            waitUntilPageIsReady();
        }
    }

    @Step("Search by {0}")
    public static boolean searchByText(String text) {
        try {
            $(byText(text)).shouldBe(Condition.visible);
            return true;
        } catch (UIAssertionError e) {
            return false;
        }
    }

    @Step("Search by {0} and click")
    public static void searchByTextAndClick(String text) {
        var elements = new ArrayList<>($$(byText(text)).filter(visible));

        if (!elements.isEmpty()) {
            elements.get(0).scrollIntoView(false).click();
        }

        waitUntilPageIsReady();
    }

    @Step("Perform click")
    public static void searchByElementAndClick(SelenideElement element) {
        waitUntilPageIsReady();
        var elements = new ArrayList<>($$(Collections.singleton(element)).filter(visible));
        if (!elements.isEmpty()) {
            elements.get(0).scrollIntoView(false).click();
        }
        waitUntilPageIsReady();
    }


    public static void waitUntilPageIsReady() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        boolean isJQuery = (boolean) js.executeScript("if (window.jQuery) { return true; } else { return false; }");

        await().atMost(15, SECONDS).pollInterval(1, SECONDS).until(() -> {
            if (js.executeScript("return document.readyState").toString().equals("complete")) {
                if (!isJQuery) {
                    return true;
                } else if ((Long) js.executeScript("return jQuery.active") == 0) {
                    return true;
                }
            }
            return false;
        });
    }


}


