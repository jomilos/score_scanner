package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

public class POLYTECHRetriever extends Retriever {

    public POLYTECHRetriever(Request request) {
        super(request);
    }

    @Override
    public void parseData(WebDriver driver, String userid) {
        Select select = new Select(driver.findElement(By.name("select2")));
        select.selectByVisibleText("09.03.02.01 Информационные системы и технологии");

        List<WebElement> buttons = driver.findElements(By.id("FIObutton"));
        for (WebElement button : buttons)
            if (button.getAttribute("value").equals("СФОРМИРОВАТЬ РЕЙТИНГОВЫЙ СПИСОК")) {
                button.click();
                break;
            }

        List<WebElement> trs = driver.findElements(By.id("pkm_"));
        for (WebElement tr : trs) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (tds.get(11).getText().trim().equals("да") || tds.get(2).getText().trim().equals(userid))
                addResult(new Result(
                        tds.get(0).getText().trim(),
                        tds.get(2).getText().trim(),
                        tds.get(12).getText().trim(),
                        tds.get(9).getText().trim()));
        }

        String seats = driver.findElement(By.xpath("/html/body/main/div/div/table[2]/tbody/tr/td/b"))
                .getText()
                .split(": ")[3];
        setSeats(seats);
    }

}
