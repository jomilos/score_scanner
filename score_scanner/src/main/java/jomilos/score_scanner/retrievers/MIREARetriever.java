package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.WebDriver;

public class MIREARetriever extends Retriever {

    public MIREARetriever(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userid) {
        WebElement tbody = driver.findElement(By.tagName("tbody"));
        List<WebElement> ths = driver.findElements(By.tagName("th"));
        int scorePos = ths.get(3).getText().equals("Соблюдены условия для платного") ? 7 : 10;
        for (WebElement tr : tbody.findElements(By.tagName("tr"))) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (tds.get(3).getText().equals("да") || tds.get(1).getText().equals(userid))
                addResult(new Result(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(2).getText(),
                        tds.get(scorePos).getText()));
        }
        List<WebElement> divs = driver.findElements(By.className("css-1ilqeby"));
        for (WebElement div : divs) {
            String p = div.findElement(By.cssSelector(".chakra-text.css-qvg8sf")).getText();
            if (p.equals("Всего мест:")) {
                String seats = div.findElement(By.cssSelector(".chakra-text.css-owk4gl")).getText();
                setSeats(seats);
                break;
            }
        }
    }
}
