import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

public abstract class CSVSelector {

    private static final String[] HEADER = "Product ID,Name,Condition,State,Price".split(",");
    private static final String OUTPUT_FILENAME = "output.csv";
    public static final String FILE_NOT_AVAILABLE = "File %s is not available";

    public static File getCheapestProducts(File directory, int productsLimit, int idLimit) {
        File[] files = DataCollector.getFilesByExtension(directory, ".csv");
        try {
            PriorityQueue<Product> products = selectCheapestProductsFromCSV(files, productsLimit, idLimit);
            return CSVCreator.createCSV(products, HEADER, OUTPUT_FILENAME);
        } catch (Exception ignored) { }
        return null;
    }

    private static PriorityQueue<Product> selectCheapestProductsFromCSV(File[] files, int productsLimit, int idLimit) throws IOException, CsvValidationException {
        PriorityQueue<Product> cheapest = new PriorityQueue<>(new ProductComparator()); //decremental sorting for correct pulling
        for (File f : files) {
            if (!f.canRead()) {
                System.out.println(String.format(FILE_NOT_AVAILABLE, f.getName()));
                continue;
            }
            PriorityQueue<Product> dataCSV = DataCollector.getSortedProducts(f);
            addProductsFromCSVtoCheapest(cheapest, dataCSV, productsLimit, idLimit);
        }
        return cheapest;
    }

    private static void addProductsFromCSVtoCheapest(
            PriorityQueue<Product> cheapest,
            PriorityQueue<Product> dataCSV,
            int productsLimit,
            int idLimit
    ) {
        while (dataCSV.size() > 0) {
            Product productFromCSV = dataCSV.poll();
            if (idLimit(productFromCSV, cheapest, idLimit)) {
                Product productFromQueue = DataCollector.getMostExpensiveProductWithSameID(productFromCSV, cheapest);
                if (productFromCSV.getPrice() < productFromQueue.getPrice()) {
                    cheapest.remove(productFromQueue);
                }
            } else {
                if (cheapest.size() == productsLimit) {
                    if (productFromCSV.isCheaper(cheapest.peek())) {
                        cheapest.poll();
                    } else {
                        //dataCSV is sorted. All following are more expensive
                        break;
                    }
                }
            }
            if (!idLimit(productFromCSV, cheapest, idLimit) && (cheapest.size() < productsLimit)) {
                cheapest.add(productFromCSV);
            }
        }
    }

    private static boolean idLimit(Product product, PriorityQueue<Product> products, int idLimit) {
        long productsWithSameIDCount = products.stream()
                .filter(p -> p.getProductID() == product.getProductID())
                .count();
        return productsWithSameIDCount == idLimit;
    }
}