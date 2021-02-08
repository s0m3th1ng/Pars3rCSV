import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public abstract class DataCollector {

    public static File[] getFilesByExtension(File directory, String extension) {
        return directory.listFiles((dir, name) -> name.endsWith(extension));
    }

    public static PriorityQueue<Product> getSortedProducts(File file) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(file));
        reader.skip(1);
        PriorityQueue<Product> products = new PriorityQueue<>(); //incremental sorting
        for (String[] data = reader.readNext(); data != null; data = reader.readNext()) {
            if (data.length != 5) {
                continue;
            }
            try {
                products.add(new Product(Integer.parseInt(data[0]), data[1], data[2], data[3], Float.parseFloat(data[4])));
            } catch (NumberFormatException ignored) { }
        }
        reader.close();
        return products; //incremental sorted by price
    }

    public static Product getMostExpensiveProductWithSameID(Product product, PriorityQueue<Product> products) {
        List<Product> productsWithSameID = products.stream()
                .filter(p -> p.getProductID() == product.getProductID())
                .sorted(new ProductComparator())
                .collect(Collectors.toList());
        return productsWithSameID.get(0);
    }

}
