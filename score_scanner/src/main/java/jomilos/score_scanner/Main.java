package jomilos.score_scanner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jomilos.score_scanner.retrievers.RetrieverFactory;
import jomilos.score_scanner.util.Config;
import jomilos.score_scanner.util.Request;

public class Main {
        public static void main(String[] args) {
                Logger logger = LogManager.getLogger(Main.class);
                Thread.currentThread().setName("Main thread");
                ExecutorService service = null;

                try {
                        Config.readConfig();

                        service = Executors.newFixedThreadPool(Config.getConfig().parallel);
                        for (Request request : Config.getConfig().requests)
                                service.execute(RetrieverFactory.newRetriever(request));

                        service.shutdown();
                        while (!service.awaitTermination(1, TimeUnit.MINUTES)) {
                        }

                } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        logger.error(sw.toString());
                        if (service != null)
                                service.shutdownNow();
                }
        }
}
