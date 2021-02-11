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
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class CSVDataCollector implements IDataCollector {

    private int headerLength;
    private char separator;

    @Override
    public PriorityQueue<Product> getSortedProducts(File file) {
        PriorityQueue<Product> products = new PriorityQueue<>(); //incremental sorting
        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
        try(CSVReader reader = new CSVReaderBuilder(
                new FileReader(file))
                .withCSVParser(parser)
                .withSkipLines(1)
                .build()
        ) {
            long lineNumber = 1;
            for (String[] data = reader.readNext(); data != null; data = reader.readNext()) {
                long productsSize = products.size();
                if (data.length == headerLength) {
                    try {
                        products.add(new Product(Integer.parseInt(data[0]), data[1], data[2], data[3], Float.parseFloat(data[4])));
                    } catch (NumberFormatException ignored) { }
                }
                if (productsSize == products.size()) {
                    log.error(String.format("Line %s in file %s contains error. Line skipped", lineNumber, file.getName()));
                }
                lineNumber++;
            }
        } catch (IOException | CsvValidationException e) {
            log.error(String.format("Error while reading file \"%s\"", file.getName()));
        }
        return products; //incremental sorted by price
    }

    @Override
    public Product getMostExpensiveProductWithSameID(Product product, PriorityQueue<Product> products) {
        List<Product> productsWithSameID = products.stream()
                .filter(p -> p.getProductID() == product.getProductID())
                .sorted(new ProductComparator())
                .collect(Collectors.toList());
        return productsWithSameID.get(0);
    }

}
