package com.example.testingframework.yahoo_finance.stock_v3.frontendtests;

import com.codeborne.selenide.*;
import com.example.testingframework.UITestsConfig;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.DateIntervalParams;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.JsonFileArgumentsProvider;
import com.example.testingframework.yahoo_finance.stock_v3.parametrized.RequestParams1;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Constants;
import com.example.testingframework.yahoo_finance.stock_v3.utils.Methods;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.example.testingframework.yahoo_finance.stock_v3.pages.ChartPage.*;
import static com.example.testingframework.yahoo_finance.stock_v3.utils.Methods.waitUntilPageIsReady;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChartUiTests extends UITestsConfig {
    private static final Map<String, String> rangeTimeUnitsMapper = Map.of(
            "1mo", "1M",
            "3mo", "3M"
            // Add more mappings as needed
    );
    private static final Map<String, String> intervalTimeUnitsMapper = Map.of(
            "1d", "1D"
            // Add more mappings as needed
    );


    @BeforeEach
    void beforeEach() {
        Methods.openBrowserIfNeeded();
        Methods.searchByText(Constants.CHART_NAME);
        Methods.searchByElementAndClick($("button.btn.secondary.accept-all"));
    }


    @Order(1)
    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "PAPAY-T1324 Login with a wrong password - error")
    @ArgumentsSource(JsonFileArgumentsProvider.class)
    public void login2(RequestParams1 params) throws IOException, ParseException, InterruptedException {

        // before
        DateIntervalParams result = convertTimeFormat(params.key());

        printDateIntervalParams(result);

        rangeElement.$$("li")
                .filterBy(Condition.text(result.range()))
                .first()
                .click();

        waitUntilPageIsReady();
        intervalBtnElement.click();





        List<SelenideElement> intervalDates = dropdownMenu.$("div#preset").$("ul#presetList").$$("li");


        // Filter the li elements that contain a button with span text "Your Desired Text"
        List<SelenideElement> filteredLiElements = intervalDates
                .stream()
                .filter(li -> li.$("button span").getText().equals(result.interval()))
                .toList();
        // You can perform other actions or validations here as needed
        filteredLiElements.forEach(SelenideElement::click);

        // Locate the li element containing a button with text "2" within the dropdown menu

        waitUntilPageIsReady();


        final List<Double> open = params.open();
        final List<Double> high = params.high();
        final List<Double> close = params.close();
        final List<Double> low = params.low();
        //when
        ;
        selectPreviousDay();

        //to.click();

        waitUntilPageIsReady();


        // Extract chart data using JavaScript
        List<String> chartDataList = getChartDataList();


        // Define regular expressions for Open, High, Low, and Close values
        Pattern openPattern = Pattern.compile("Open(\\d+\\.\\d+)");
        Pattern highPattern = Pattern.compile("High(\\d+\\.\\d+)");
        Pattern lowPattern = Pattern.compile("Low(\\d+\\.\\d+)");
        Pattern closePattern = Pattern.compile("Close(\\d+\\.\\d+)");
        String openValue = null;
        String highValue = null;
        String lowValue = null;
        String closeValue = null;


        for (String data : chartDataList) {
            // Create Matchers
            Matcher openMatcher = openPattern.matcher(data);
            Matcher highMatcher = highPattern.matcher(data);
            Matcher lowMatcher = lowPattern.matcher(data);
            Matcher closeMatcher = closePattern.matcher(data);

            // Find and print the values
            if (openMatcher.find()) {
                openValue = openMatcher.group(1);
                System.out.println("Open: " + openValue);
            }

            if (highMatcher.find()) {
                highValue = highMatcher.group(1);
                System.out.println("High: " + highValue);
            }

            if (lowMatcher.find()) {
                lowValue = lowMatcher.group(1);
                System.out.println("Low: " + lowValue);
            }

            if (closeMatcher.find()) {
                closeValue = closeMatcher.group(1);
                System.out.println("Close: " + closeValue);
            }
        }

        // Assuming you have existing high1 and open1 strings
        assertEquals(close.get(close.size()-1),Double.valueOf(closeValue), "Open value mismatch");
        assertEquals(low.get(low.size()-1),Double.valueOf(lowValue), "Open value mismatch");
        assertEquals(high.get(high.size()-1),Double.valueOf(highValue), "Open value mismatch");
        assertEquals(open.get(open.size()-1),Double.valueOf(openValue), "Open value mismatch");


    }

    private static List<String> getChartDataList() {
        List<String> chartDataList = (List<String>) ((JavascriptExecutor) getWebDriver()).executeScript(
                "var elements = document.querySelectorAll('.stx-subholder');" +
                        "var textContentList = [];" +
                        "for (var i = 0; i < elements.length; i++) {" +
                        "  textContentList.push(elements[i].textContent);" +
                        "}" +
                        "return textContentList;");
        return chartDataList;
    }

    private static void selectPreviousDay() {


        Actions actions = new Actions(getWebDriver());


        // Perform the click action at the right-down corner of the element
        actions.moveToElement(chartDataSubHolder, chartDataSubHolder.getLocation().getX(), chartDataSubHolder.getSize().getHeight()/2)
                .click().build().perform();
        // Define the number of pixels to move horizontally (e.g., 1000 pixels)
        int totalPixelsToMove = 1000;

        // Create a date formatter for today's date
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate todayDate = LocalDate.now();

// Subtract one day
        LocalDate previousDayDate = todayDate.minusDays(1);

// Format the dates as strings
        String previousDayDateString = previousDayDate.format(dateFormat);

        // Variables to track whether the condition is met
        boolean conditionMet = false;

        int pixelsMoved = 0; // Initialize pixelsMoved

        do {
            // Move the mouse to the right by 10 pixels
            actions.moveToElement(mainChartHolder, pixelsMoved, 0).perform();


            //final String format = dateFormat.format(previousDayDateString);
            if (previousDayDateString.contains(selectedDate.getText())) {
                conditionMet = true;
                break; // Exit the loop if condition is met
            }

            // Increment pixelsMoved by 10 for the next iteration
            pixelsMoved += 10;
        } while (pixelsMoved < totalPixelsToMove && !conditionMet);



    }

    // Verify the chart data (customize assertion logic as needed)


    public static DateIntervalParams convertTimeFormat(String input) {
        String[] parts = input.split("_");
        if (parts.length != 2) {
            return new DateIntervalParams("Invalid Format", "Invalid Format");
        }

        String range = parts[0];
        String interval = parts[1];

        range = rangeTimeUnitsMapper.getOrDefault(range, "Invalid Format");
        interval = intervalTimeUnitsMapper.getOrDefault(interval, "Invalid Format");

        return new DateIntervalParams(range, interval);
    }

    public static void printDateIntervalParams(DateIntervalParams result) {
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

}
