import com.opencsv.CSVReader;
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
public class DataCollector implements IDataCollector {

    @Override
    public File[] getFilesByExtension(File directory, String extension) {
        return directory.listFiles((dir, name) -> name.endsWith(extension));
    }

    @Override
    public PriorityQueue<Product> getSortedProducts(File file, int headerLength) throws IOException, CsvValidationException {
        PriorityQueue<Product> products = new PriorityQueue<>(); //incremental sorting
        try(CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.skip(1);
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
