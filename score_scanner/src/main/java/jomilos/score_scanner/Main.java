package jomilos.score_scanner;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import jomilos.score_scanner.retrievers.RetrieverThread;
import jomilos.score_scanner.retrievers.RetrieverThreadFactory;
import jomilos.score_scanner.util.Config;
import jomilos.score_scanner.util.Request;

public class Main {
        public static void main(String[] args) {
                Logger logger = LogManager.getLogger(Main.class);
                Thread.currentThread().setName("Main thread");
                List<RetrieverThread> retrievers = new ArrayList<>();

                try {
                        try (FileReader reader = new FileReader(
                                        "/home/andrey/Projects/score_scanner_run/score_scanner.properties",
                                        StandardCharsets.UTF_8)) {
                                JavaPropsMapper mapper = new JavaPropsMapper();
                                Config config = mapper.readValue(reader, Config.class);
                                for (Request request : config.requests)
                                        retrievers.add(RetrieverThreadFactory.newRetrieverThread(request));
                        }

                        for (Thread t : retrievers)
                                t.start();

                        for (Thread t : retrievers)
                                t.join();

                } catch (

                Exception e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        logger.error(sw.toString());
                }
        }
}
