package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.WebDriver;

public class MPGURetriever extends Retriever {

    public MPGURetriever(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userid) {
        WebElement tbody = driver.findElement(By.tagName("tbody"));
        for (WebElement tr : tbody.findElements(By.tagName("tr"))) {
            if (tr.getAttribute("class").equals("R16")) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            // TODO Добавить поиск по "заключен договор" когда они будут
            //if (tds.get(3).getText().equals("да") || tds.get(1).getText().equals(userID))
                addResult(new Result(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(3).getText(),
                        tds.get(7).getText()));
            }
        }
        List<WebElement> headFields = driver.findElements(By.className("R2C0"));
        for (WebElement headField : headFields) {
            if (headField.getText().contains("Контрольные цифры приёма")) {
                setSeats(headField.getText().split(": ")[1]);
                break;
            }
        }
    }
}
