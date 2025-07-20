package jomilos.score_scanner.retrievers;

import jomilos.score_scanner.util.Request;

public class RetrieverThreadFactory {
    public static RetrieverThread newRetrieverThread(Request request) {
        return switch (request.university) {
            case "МИРЭА" -> new MIREARetrieverThread(request);
            case "МИСИС" -> new MISISReatrieverThread(request);
            case "МПГУ" -> new MPGURetrieverThread(request);
            case "ПОЛИТЕХ" -> new POLYTECHRetrieverThread(request);
            case "РГУК" -> new RGUKRetrieverThread(request);
            default -> throw new RuntimeException("Неизвестный университет: " + request.university);
        };
    }
}
