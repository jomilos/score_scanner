package jomilos.score_scanner.util;

import java.util.List;

public class Config {
    public List<Request> requests;
    public String browser;

    private static Config config;

    public static void setConfig(Config config) {
        Config.config = config;
    }

    public static Config getConfig() {
        return Config.config;
    }
}
