package jomilos.score_scanner.retrievers;

import jomilos.score_scanner.util.Request;

public class RetrieverThreadFactory {
    public static RetrieverThread newRetrieverThread(Request request) {
        return switch (Universities.getUniversityByUrl(request.url)) {
            case MIREA -> new MIREARetrieverThread(request);
            case MISIS -> new MISISReatrieverThread(request);
            case MPGU -> new MPGURetrieverThread(request);
            case POLYTECH -> new POLYTECHRetrieverThread(request);
            case RGUK -> new RGUKRetrieverThread(request);
            case UNKNOWN -> throw new RuntimeException("Неизвестный университет: " + request.url);
        };
    }
}
