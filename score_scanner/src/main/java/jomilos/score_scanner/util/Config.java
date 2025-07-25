package jomilos.score_scanner.util;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import jomilos.score_scanner.exceptions.InvalidConfigurationException;

public class Config {
    public List<Request> requests;
    public String browser;
    public int parallel;

    @JsonIgnore
    public Browser browserType;

    private static Config config;

    public static Config getConfig() {
        return Config.config;
    }

    public static void readConfig() throws IOException, InvalidConfigurationException {
        try (FileReader reader = new FileReader(
                System.getProperty("score_scanner.home", System.getProperty("user.home"))
                        + "/score_scanner.properties",
                StandardCharsets.UTF_8)) {
            JavaPropsMapper mapper = new JavaPropsMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Config.config = mapper.readValue(reader, Config.class);
            config.validate();
        }
    }

    private void validate() throws InvalidConfigurationException {
        validateBrowser();
        validetParallel();
        validateRequest();
    }

    private void validateBrowser() throws InvalidConfigurationException {
        if (config.browser == null || config.browser.isBlank())
            throw new InvalidConfigurationException("Не указан браузер.");

        Browser browserType = Browser.getBrowserByName(config.browser);
        if (browserType == Browser.UNKNOWN)
            throw new InvalidConfigurationException("Неизвестный браузер " + config.browser);

        config.browserType = browserType;
    }

    private void validetParallel() {
        if (parallel == 0) {
            parallel = 10;
        }
    }

    private void validateRequest() throws InvalidConfigurationException {
        for (int i = 0; i < requests.size(); i++) {
            Request request = config.requests.get(i);

            if (request.url == null || request.url.isBlank())
                throw new InvalidConfigurationException("Не задан url по индексу " + i);

            University universityType = University.getUniversityByUrl(request.url);
            if (universityType == University.UNKNOWN)
                throw new InvalidConfigurationException("Неизвестный ВУЗ по индексу " + i);
            request.universityType = universityType;

            if (request.university == null || request.university.isBlank()) {
                request.university = universityType.getDefaultName();
            }
        }
    }
}
