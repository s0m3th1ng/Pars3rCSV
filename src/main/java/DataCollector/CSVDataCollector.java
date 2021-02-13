package DataCollector;

import Product.*;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

@Slf4j
@AllArgsConstructor
public class CSVDataCollector implements IDataCollector {

    private static String ERROR_MESSAGE = "Error while reading file \"%s\"";
    private static final String LINE_SKIPPED = "Line %s in file %s contains error. Line skipped";

    private String[] header;
    private char separator;

    @Override
    public PriorityQueue<Product> getSortedProducts(File file) throws IOException {
        PriorityQueue<Product> products = new PriorityQueue<>(); //incremental sorting
        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(file))
                .withCSVParser(parser)
                .build()
        ) {
            String[] fileHeader = reader.readNext();
            if (!Arrays.equals(fileHeader, header)) {
                ERROR_MESSAGE = "Incorrect header in file %s";
                throw new IOException();
            }
            long lineNumber = 1;
            for (String[] data = reader.readNext(); data != null; data = reader.readNext()) {
                long productsSize = products.size();
                if (data.length == header.length) {
                    try {
                        products.add(new Product(Integer.parseInt(data[0]), data[1], data[2], data[3], Float.parseFloat(data[4])));
                    } catch (NumberFormatException ignored) { }
                }
                if (productsSize == products.size()) {
                    log.error(String.format(LINE_SKIPPED, lineNumber, file.getName()));
                }
                lineNumber++;
            }
        } catch (IOException | CsvValidationException e) {
            log.error(String.format(ERROR_MESSAGE, file.getName()));
            throw new IOException(String.format(ERROR_MESSAGE, file.getName()));
        }
        return products; //incremental sorted by price
    }

}
