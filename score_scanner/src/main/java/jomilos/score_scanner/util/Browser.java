package jomilos.score_scanner.util;

public enum Browser {
    FIREFOX,
    CHROME,
    EDGE,
    UNKNOWN;

    public static Browser getBrowserByName(String name) {
        return switch(name.trim().toLowerCase()) {
            case "firefox" -> FIREFOX;
            case "chrome" -> CHROME;
            case "edge" -> EDGE;
            default -> UNKNOWN;
        };
    }
}
