package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.WebDriver;

public class MISISReatriever extends Retriever {

    public MISISReatriever(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userid) {
        WebElement tbody = driver.findElement(By.tagName("tbody"));
        WebElement finance = driver.findElement(By.tagName("finans"));
        int yesPos = 16;
        if (finance.getText().equals("внебюджет"))
            yesPos = 17;
        for (WebElement tr : tbody.findElements(By.tagName("tr"))) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (!tds.get(yesPos).getText().isBlank() || tds.get(1).getText().equals(userid))
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
