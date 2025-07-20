package jomilos.score_scanner.retrievers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import jomilos.score_scanner.util.Request;
import jomilos.score_scanner.util.Result;

import org.openqa.selenium.firefox.FirefoxDriver;

public abstract class RetrieverThread extends Thread {

    private String URL;
    private String userID;
    private String university;
    private String specialization;
    private List<Result> result;
    private String seats;
    private final Logger logger = LogManager.getLogger(getClass());

    private static final Object MONITOR = new Object();
    private static final int WIDTH = 40;

    public RetrieverThread(Request request) {
        this.URL = request.url;
        this.userID = request.userid;
        this.university = request.university;
        this.specialization = request.specialization;
        setName(university + " " + specialization);
    }

    @Override
    public void run() {
        WebDriver driver = null;
        logger.info("Поток запущен");
        try {
            logger.info("Запускаю браузер");
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("-headless");
            driver = new FirefoxDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));

            logger.info("Запрашиваю страницу конкурса {}", URL);
            driver.get(URL);
            result = new ArrayList<>();

            logger.info("Анализирую страницу конкурса {}", URL);
            parseData(driver, userID);

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
                if (result.get(i).id().equals(userID))
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

    public abstract void parseData(WebDriver driver, String userID);
}
