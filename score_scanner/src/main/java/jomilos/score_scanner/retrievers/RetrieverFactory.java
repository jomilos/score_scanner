package jomilos.score_scanner.retrievers;

import jomilos.score_scanner.util.Request;

public class RetrieverFactory {
    public static Retriever newRetrieverThread(Request request) {
        return switch (Universities.getUniversityByUrl(request.url)) {
            case MIREA -> new MIREARetriever(request);
            case MISIS -> new MISISReatriever(request);
            case MPGU -> new MPGURetriever(request);
            case POLYTECH -> new POLYTECHRetriever(request);
            case RGUK -> new RGUKRetriever(request);
            case UNKNOWN -> throw new RuntimeException("Неизвестный университет: " + request.url);
        };
    }
}
