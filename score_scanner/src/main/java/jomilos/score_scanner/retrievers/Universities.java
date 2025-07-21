package jomilos.score_scanner.retrievers;

public enum Universities {
    MIREA("МИРЭА"),
    RGUK("РГУК"),
    POLYTECH("Политех"),
    MISIS("МИСИС"),
    MPGU("МПГУ"),
    UNKNOWN(null);

    private String defaultName;

    private Universities(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public static Universities getUniversityByUrl(String url) {
        String domainName = url.split("/")[2];
        return switch (domainName) {
                case "priem.mirea.ru" -> Universities.MIREA;
                case "mospolytech.ru" -> Universities.POLYTECH;
                case "lists.rguk.ru" -> Universities.RGUK;
                case "misis.ru" -> Universities.MISIS;
                case "epk25.mpgu.su" -> Universities.MPGU;
                default -> Universities.UNKNOWN;
        };
    }
}
