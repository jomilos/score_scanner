package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.WebDriver;

public class RGUKRetrieverThread extends RetrieverThread {

    public RGUKRetrieverThread(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userid) {
        driver.manage().window().maximize();
        WebElement tbody = driver.findElement(By.tagName("tbody"));
        for (WebElement tr : tbody.findElements(By.tagName("tr"))) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            String id = tds.get(2).getText().split("\n")[0];
            if (tds.get(11).getText().equals("Да") || id.equals(userid))
                addResult(new Result(
                        tds.get(0).getText(),
                        id,
                        tds.get(3).getText(),
                        tds.get(10).getText()));
        }
        WebElement header = driver.findElement(By.className("main-block__header"));
        for (WebElement p : header.findElements(By.tagName("p")))
            if (p.getText().contains("Количество мест:"))
                setSeats(p.getText().split(": ")[1]);

    }

}
