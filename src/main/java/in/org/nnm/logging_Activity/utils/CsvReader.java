package in.org.nnm.logging_Activity.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class CsvReader {

    private static final Logger logger = LogManager.getLogger(CsvReader.class);

    public void display() {
        String filename="src/main/resources/logs.csv";
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            lines.forEach(logger::info);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }
    }
}
