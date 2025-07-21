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

import jomilos.score_scanner.util.Config;
import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

public abstract class RetrieverThread extends Thread {

    private String URL;
    private String userid;
    private String university;
    private String specialization;
    private List<Result> result;
    private String seats;
    private WebDriver driver;
    private final Logger logger = LogManager.getLogger(getClass());

    private static final Object MONITOR = new Object();
    private static final int WIDTH = 40;

    public RetrieverThread(Request request) {
        URL = request.url;
        userid = request.userid;
        university = request.university;
        if (university == null)
            university = Universities.getUniversityByUrl(request.url).getDefaultName();
        specialization = request.specialization;
        setName(university + " " + specialization);
    }

    @Override
    public void run() {
        logger.info("Поток запущен");
        try {
            logger.info("Запускаю браузер " + Config.getConfig().browser);
            startWebDriver();

            logger.info("Запрашиваю страницу конкурса {}", URL);
            retrieveData();

            logger.info("Анализирую страницу конкурса {}", URL);
            parseData(driver, userid);

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
            renderHeader(university, "*");
            renderHeader(specialization, "-");
            System.out.println("Ошибка обработки данных!");
            System.out.println();
        }
    }

    private void renderResult() {
        synchronized (MONITOR) {
            renderHeader(university, "*");
            renderHeader(specialization, "-");
            System.out.println("\tN\tИД\tПр\tБаллы");
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).id().equals(userid))
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
        String browser = Config.getConfig().browser;
        switch (browser) {
            case "FireFox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-headless");
                driver = new FirefoxDriver(options);
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
            }
            case "Chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless=new");
                driver = new ChromeDriver(options);
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
            }
            case "Edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--headless=new");
                driver = new EdgeDriver(options);
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
            }
            default -> throw new RuntimeException("Неизвестный браузер: " + browser);
        }
    }

    private void retrieveData() {
        driver.get(URL);
        result = new ArrayList<>();
    }

    public abstract void parseData(WebDriver driver, String userID);
}
