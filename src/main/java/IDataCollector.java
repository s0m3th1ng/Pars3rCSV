import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

public interface IDataCollector {

    File[] getFilesByExtension(File directory, String extension);
    PriorityQueue<Product> getSortedProducts(File file, int headerLength) throws IOException, CsvValidationException;
    Product getMostExpensiveProductWithSameID(Product product, PriorityQueue<Product> products);

}
