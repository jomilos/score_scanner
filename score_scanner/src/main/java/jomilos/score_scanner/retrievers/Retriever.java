package jomilos.score_scanner.retrievers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import jomilos.score_scanner.util.Browser;
import jomilos.score_scanner.util.Config;
import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

public abstract class Retriever implements Runnable {

    private List<Result> result;
    private String seats;
    private WebDriver driver;
    private final Logger logger = LogManager.getLogger(getClass());

    private static final Object MONITOR = new Object();
    private static final int WIDTH = 40;
    private Request request;

    public Retriever(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(request.university + " " + request.specialization);
        logger.info("Поток запущен");
        try {
            logger.info("Запускаю браузер " + Config.getConfig().browser);
            startWebDriver();

            logger.info("Запрашиваю страницу конкурса {}", request.url);
            retrieveData();

            logger.info("Анализирую страницу конкурса {}", request.url);
            parseData(driver, request.userid);

            logger.info("Готовлю результат обработки данных");
            renderResult();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error("Ошибка обработки данных!\n{}", sw.toString());
            renderError();
        } finally {
            logger.info("Закрываю браузер");
            if (driver != null)
                driver.quit();
        }
    }

    protected void addResult(Result record) {
        result.add(record);
    }

    protected void setSeats(String seats) {
        this.seats = seats;
    }

    private void renderError() {
        synchronized (MONITOR) {
            renderHeader(request.university, "*");
            renderHeader(request.specialization, "-");
            System.out.println("Ошибка обработки данных!");
            System.out.println();
        }
    }

    private void renderResult() {
        synchronized (MONITOR) {
            renderHeader(request.university, "*");
            renderHeader(request.specialization, "-");
            System.out.println("\tN\tИД\tПр\tБаллы");
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).id().equals(request.userid))
                    System.out.print((i + 1) + "*\t");
                else
                    System.out.print((i + 1) + "\t");
                System.out.println(result.get(i).position() + "\t" +
                        result.get(i).id() + "\t" +
                        result.get(i).priority() + "\t" +
                        result.get(i).score());
            }
            System.out.println("Количество мест: " + seats);
            System.out.println();
        }
    }

    private void renderHeader(String header, String fill) {
        header = "  " + header + "  ";
        int fillLength = (int) ((WIDTH - header.length()) / 2);
        String result = fill.repeat(fillLength) + header + fill.repeat(fillLength);
        if (result.length() < WIDTH)
            result += fill;
        System.out.println(result);
    }

    private void startWebDriver() {
        // TODO отрефакторить в фабрику?
        Browser browser = Config.getConfig().browserType;
        switch (browser) {
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-headless");
                driver = new FirefoxDriver(options);
            }
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless=new");
                driver = new ChromeDriver(options);
            }
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--headless=new");
                driver = new EdgeDriver(options);
            }
            default -> throw new RuntimeException("Не могу запустить драйвер для " + browser);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
    }

    private void retrieveData() {
        driver.get(request.url);
        result = new ArrayList<>();
    }

    public abstract void parseData(WebDriver driver, String userID);
}
