package jomilos.score_scanner.retrievers;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

public class POLYTECHRetriever extends Retriever { // TODO доделать для бюджета - парсинг общего кол-ва мест и таблицы

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

        WebElement isFin = driver
                .findElement(By.xpath("/html/body/main/div/div/table[3]/tbody/tr/td/div/table/tbody/tr[1]/td[12]"));
        int yesPos = 12;
        int priorityPos = 13;
        int scorePos = 10;
        if (isFin.getAttribute("title").contains("договор")) {
            yesPos = 11;
            priorityPos = 12;
            scorePos = 9;
        }
        List<WebElement> trs = driver.findElements(By.id("pkm_"));
        for (WebElement tr : trs) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            if (tds.get(yesPos).getText().trim().equals("да") || tds.get(2).getText().trim().equals(userid))
                addResult(new Result(
                        tds.get(0).getText().trim(),
                        tds.get(2).getText().trim(),
                        tds.get(priorityPos).getText().trim(),
                        tds.get(scorePos).getText().trim()));
        }

        String seats = (driver.findElement(By.xpath("/html/body/main/div/div/table[2]/tbody/tr/td/b"))
                .getText()
                .split(":")[3])
                .split("\\n")[0];
        setSeats(seats);
    }

}
