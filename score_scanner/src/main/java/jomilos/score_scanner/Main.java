package jomilos.score_scanner;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import jomilos.score_scanner.retrievers.RetrieverFactory;
import jomilos.score_scanner.util.Config;
import jomilos.score_scanner.util.Request;

public class Main {
        public static void main(String[] args) {
                Logger logger = LogManager.getLogger(Main.class);
                Thread.currentThread().setName("Main thread");
                int parallel = 10;

                try {
                        try (FileReader reader = new FileReader(
                                        System.getProperty("score_scanner.home", System.getProperty("user.home"))
                                                        + "/score_scanner.properties",
                                        StandardCharsets.UTF_8)) {
                                JavaPropsMapper mapper = new JavaPropsMapper();
                                Config.setConfig(mapper.readValue(reader, Config.class));
                        }

                        if (Config.getConfig().parallel != 0)
                                parallel = Config.getConfig().parallel;

                        ExecutorService service = Executors.newFixedThreadPool(parallel);
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
                }
        }
}
