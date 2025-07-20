package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.WebDriver;

public class MISISReatrieverThread extends RetrieverThread {

    public MISISReatrieverThread(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userID) {
        WebElement tbody = driver.findElement(By.tagName("tbody"));
        for (WebElement tr : tbody.findElements(By.tagName("tr"))) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (!tds.get(15).getText().isEmpty() || tds.get(1).getText().equals(userID))
                addResult(new Result(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(3).getText(),
                        tds.get(4).getText()));
        }

        String seats = driver.findElement(By.tagName("itog")).getText();
        setSeats(seats);
    }
}
