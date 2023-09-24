package com.example.testingframework.yahoo_finance.stock_v3.frontendtests;

import com.codeborne.selenide.*;
import com.example.testingframework.UITestsConfig;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.DateRangeIntervalParams;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.IntervalOneDayArgumentsProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.DataToVerify;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;
import com.example.testingframework.yahoo_finance.stock_v3.utils.UiMethods;
import com.example.testingframework.yahoo_finance.stock_v3.utils.tags.UiTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.example.testingframework.yahoo_finance.stock_v3.pages.ChartPage.*;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.ChartDataProcessor.processChartData;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.DateMapper.intervalTimeUnitsMapper;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.DateMapper.rangeTimeUnitsMapper;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.UiMethods.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UiTests
public class ChartUiTests extends UITestsConfig {


    @BeforeEach
    void beforeEach() {
        openBrowserIfNeeded();
        dealWithCookiesSettings();
        searchByText(Constants.CHART_NAME);
    }

    // Currently it is designed like that it will verify the data only against previous date.
    //Of course it needs to be changed. Just wanted to show the idea
    @Order(1)
    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "Verify chart using interval 1 day and range {0}")
    @ArgumentsSource(IntervalOneDayArgumentsProvider.class)
    public void verifyChartUsingIntervalOneDay(DataToVerify dataToVerify) {

        // before
        DateRangeIntervalParams rangeIntervalParams = convertRangeInterval(dataToVerify.key());
        printRangeIntervalParams(rangeIntervalParams);
        final List<Double> open = dataToVerify.open();
        final List<Double> high = dataToVerify.high();
        final List<Double> close = dataToVerify.close();
        final List<Double> low = dataToVerify.low();
        //then
        selectNeededRangeFromList(rangeIntervalParams.range());
        selectNeededIntervalFromList(rangeIntervalParams.interval());
        // Currently it is designed like that it will verify the data only against previous date.
        //Of course it needs to be changed. Just wanted to show the idea
        selectNeededDay(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        //when
        List<String> chartDataList = getChartDataList();

        final Map<String, Double> s = processChartData(chartDataList);

        // Currently it is designed like that it will verify the data only against previous date.
        //Of course it needs to be changed. Just wanted to show the idea
        assertEquals(close.get(close.size() - 1), s.get("Close"), "Close value mismatch");
        assertEquals(low.get(low.size() - 1), s.get("Low"), "Low value mismatch");
        assertEquals(high.get(high.size() - 1), s.get("High"), "High value mismatch");
        assertEquals(open.get(open.size() - 1), s.get("Open"), "Open value mismatch");


    }

    private void selectNeededIntervalFromList(String interval) {
        intervalBtnElement.click();
        List<SelenideElement> intervalDates = dropdownMenu.$("div#preset").$("ul#presetList").$$("li")
                .stream()
                .filter(li -> li.$("button span").getText().equals(interval))
                .toList();
        intervalDates.forEach(SelenideElement::click);
        waitUntilPageIsReady();
    }

    private void selectNeededRangeFromList(String range) {
        rangeElement.$$("li")
                .filterBy(Condition.text(range))
                .first()
                .click();

        waitUntilPageIsReady();
    }

    private List<String> getChartDataList() {
        List<String> chartDataList = (List<String>) ((JavascriptExecutor) getWebDriver()).executeScript(
                "var elements = document.querySelectorAll('.stx-subholder');" +
                        "var textContentList = [];" +
                        "for (var i = 0; i < elements.length; i++) {" +
                        "  textContentList.push(elements[i].textContent);" +
                        "}" +
                        "return textContentList;");
        return chartDataList;
    }

    private void selectNeededDay(String previousDayDateString) {
        int maxPixelsToMove = chartDataSubHolder.getSize().getWidth();
        Actions actions = new Actions(getWebDriver());
        boolean conditionMet = false;

        int pixelsMoved = 0;

        do {
            actions.moveToElement(mainChartHolder, pixelsMoved, 0).perform();

            if (previousDayDateString.contains(selectedDate.getText())) {
                conditionMet = true;
                System.out.println("Date selected: " + previousDayDateString);
                break;
            }
            pixelsMoved += 10;
        } while (pixelsMoved < maxPixelsToMove && !conditionMet);
        waitUntilPageIsReady();

    }


    private DateRangeIntervalParams convertRangeInterval(String input) {
        String[] parts = input.split("_");
        if (parts.length != 2) {
            return new DateRangeIntervalParams("Invalid Format", "Invalid Format");
        }

        String range = parts[0];
        String interval = parts[1];

        range = rangeTimeUnitsMapper.getOrDefault(range, "Invalid Format");
        interval = intervalTimeUnitsMapper.getOrDefault(interval, "Invalid Format");

        return new DateRangeIntervalParams(range, interval);
    }

    private void printRangeIntervalParams(DateRangeIntervalParams result) {
        String range = result.range();
        String interval = result.interval();

        switch (range) {
            case "Invalid Format", "Unsupported Range" -> System.out.println("Error: Invalid Range Format");
            default -> System.out.println("Range: " + range);
        }

        switch (interval) {
            case "Invalid Format", "Unsupported Interval" -> System.out.println("Error: Invalid Interval Format");
            default -> System.out.println("Interval: " + interval);
        }

    }

    private void dealWithCookiesSettings() {
        UiMethods.searchByElementAndClick($(scrollDownButton));
        UiMethods.searchByElementAndClick($(acceptAllCookies));
    }


}
