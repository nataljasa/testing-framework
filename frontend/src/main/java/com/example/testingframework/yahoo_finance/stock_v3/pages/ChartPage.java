package com.example.testingframework.yahoo_finance.stock_v3.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Component
public class ChartPage {
    public static final SelenideElement rangeElement = $(By.cssSelector("ul[data-test=rangeList]"));
    public static final SelenideElement intervalBtnElement = $(By.xpath("(//span[contains(@class, 'intervalBtn')])[2]"));;
    public static final  SelenideElement dropdownMenu = $(By.cssSelector("div[data-test='intervalSelector-dd-menu']"));
    public static final  SelenideElement chartDataSubHolder = $(".stx-subholder");
    public static final  SelenideElement mainChartHolder = $(".stx-holder.stx-panel-chart");
    public static final  SelenideElement selectedDate = $(".stx-float-date.floatDate");
    public static final  SelenideElement acceptAllCookies = $("button.btn.secondary.accept-all");
    public static final SelenideElement scrollDownButton = $("#scroll-down-btn");


}
