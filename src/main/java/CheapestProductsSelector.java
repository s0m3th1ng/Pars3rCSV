import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

@Slf4j
public class CheapestProductsSelector {

    private static final String FILE_NOT_AVAILABLE = "File %s is not available";

    private CSVDataCollector collector;
    private CSVCreator creator;

    public CheapestProductsSelector(String[] header, String outputFilename) {
        this.collector = new CSVDataCollector(header.length);
        this.creator = new CSVCreator(header, outputFilename);
    }

    public File getCheapestProducts(File directory, int productsLimit, int idLimit) {
        File[] files = collector.getFilesByExtension(directory, ".csv");
        PriorityQueue<Product> products = selectCheapestProductsFromFiles(files, productsLimit, idLimit);
        return creator.createFile(products);
    }

    private PriorityQueue<Product> selectCheapestProductsFromFiles(File[] files, int productsLimit, int idLimit) {
        PriorityQueue<Product> cheapest = new PriorityQueue<>(new ProductComparator()); //decremental sorting for correct pulling
        for (File f : files) {
            if (!f.canRead()) {
                log.error(String.format(FILE_NOT_AVAILABLE, f.getName()));
                continue;
            }
            PriorityQueue<Product> dataFromFile = collector.getSortedProducts(f);
            addProductsFromFileToCheapest(cheapest, dataFromFile, productsLimit, idLimit);
        }
        return cheapest;
    }

    private void addProductsFromFileToCheapest(
            PriorityQueue<Product> cheapest,
            PriorityQueue<Product> dataFromFile,
            int productsLimit,
            int idLimit
    ) {
        while (dataFromFile.size() > 0) {
            Product productFromCSV = dataFromFile.poll();
            if (idLimit(productFromCSV, cheapest, idLimit)) {
                Product productFromQueue = collector.getMostExpensiveProductWithSameID(productFromCSV, cheapest);
                if (productFromCSV.getPrice() < productFromQueue.getPrice()) {
                    cheapest.remove(productFromQueue);
                }
            } else {
                if (cheapest.size() == productsLimit) {
                    if (productFromCSV.isCheaper(cheapest.peek())) {
                        cheapest.poll();
                    } else {
                        //dataFromFile is sorted. All following are more expensive
                        break;
                    }
                }
            }
            if (!idLimit(productFromCSV, cheapest, idLimit) && (cheapest.size() < productsLimit)) {
                cheapest.add(productFromCSV);
            }
        }
    }

    private boolean idLimit(Product product, PriorityQueue<Product> products, int idLimit) {
        long productsWithSameIDCount = products.stream()
                .filter(p -> p.getProductID() == product.getProductID())
                .count();
        return productsWithSameIDCount == idLimit;
    }
}