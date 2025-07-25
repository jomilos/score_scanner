package jomilos.score_scanner.util;

public enum University {
    MIREA("МИРЭА"),
    RGUK("РГУК"),
    POLYTECH("Политех"),
    MISIS("МИСИС"),
    MPGU("МПГУ"),
    UNKNOWN("Неизвестный ВУЗ");

    private String defaultName;

    private University(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public static University getUniversityByUrl(String url) {
        String domainName = url.split("/")[2];
        return switch (domainName) {
                case "priem.mirea.ru" -> University.MIREA;
                case "mospolytech.ru" -> University.POLYTECH;
                case "lists.rguk.ru" -> University.RGUK;
                case "misis.ru" -> University.MISIS;
                case "epk25.mpgu.su" -> University.MPGU;
                default -> University.UNKNOWN;
        };
    }
}
