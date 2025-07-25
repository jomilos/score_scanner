package jomilos.score_scanner.retrievers;

import jomilos.score_scanner.util.Request;

public class RetrieverFactory {
    public static Retriever newRetriever(Request request) {
        return switch (request.universityType) {
            case MIREA -> new MIREARetriever(request);
            case MISIS -> new MISISReatriever(request);
            case MPGU -> new MPGURetriever(request);
            case POLYTECH -> new POLYTECHRetriever(request);
            case RGUK -> new RGUKRetriever(request);
            default -> throw new RuntimeException("Не могу создать ретривер для " + request.url);
        };
    }
}
