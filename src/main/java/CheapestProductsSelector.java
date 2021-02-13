import DataCollector.IDataCollector;
import FileCreator.IFileCreator;
import Product.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

@Slf4j
@AllArgsConstructor
public class CheapestProductsSelector {

    private static final String FILE_DOES_NOT_EXIST = "File %s does not exist";

    private IDataCollector collector;
    private IFileCreator creator;

    public File getCheapestProducts(@NotNull File file, int productsLimit, int idLimit) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new IOException(String.format(FILE_DOES_NOT_EXIST, file.getName()));
        }
        PriorityQueue<Product> cheapest = new PriorityQueue<>(new ProductComparator()); //decremental sorting for correct pulling;
        if (productsLimit < 1 || idLimit < 1) {
            return creator.createFile(cheapest);
        }
        PriorityQueue<Product> dataFromFile = collector.getSortedProducts(file);
        addProductsFromFileToCheapest(cheapest, dataFromFile, productsLimit, idLimit);
        return creator.createFile(cheapest);
    }

    private void addProductsFromFileToCheapest(
            PriorityQueue<Product> cheapest,
            PriorityQueue<Product> dataFromFile,
            int productsLimit,
            int idLimit
    ) {
        while (dataFromFile.size() > 0) {
            Product productFromCSV = dataFromFile.poll();
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