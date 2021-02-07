import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public abstract class CSVSelector {

    private static final String[] header = "Product ID,Name,Condition,State,Price".split(",");

    public static File parseFolder(File directory, int productsLimit, int idLimit) {
        File[] files = getAllCSV(directory);
        try {
            PriorityQueue<Product> products = selectCheapest(files, productsLimit, idLimit);
            return createCSV(products);
        } catch (Exception ignored) { }
        return null;
    }

    private static File[] getAllCSV(File directory) {
        return directory.listFiles((dir, name) -> name.endsWith(".csv"));
    }

    private static PriorityQueue<Product> selectCheapest(File[] files, int productsLimit, int idLimit) throws IOException, CsvValidationException {
        PriorityQueue<Product> cheapest = new PriorityQueue<>(); //decremental sorting for correct pulling
        for (File f : files) {
            if (!f.canRead()) {
                System.out.println(String.format("File %s is not available", f.getName()));
                continue;
            }
            PriorityQueue<Product> dataCSV = getSortedData(f);
            addProductsFromCSV(cheapest, dataCSV, productsLimit, idLimit);
        }
        return cheapest;
    }

    private static PriorityQueue<Product> getSortedData(File file) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(file));
        reader.skip(1);
        PriorityQueue<Product> products = new PriorityQueue<>(new ProductComparator()); //incremental sorting
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

    private static void addProductsFromCSV(
            PriorityQueue<Product> cheapest,
            PriorityQueue<Product> dataCSV,
            int productsLimit,
            int idLimit
    ) {
        while (dataCSV.size() > 0) {
            Product productFromCSV = dataCSV.poll();
            if (idLimit(productFromCSV, cheapest, idLimit)) {
                Product productFromQueue = getMostExpensiveProductWithSameID(productFromCSV, cheapest);
                if (productFromCSV.getPrice() < productFromQueue.getPrice()) {
                    cheapest.remove(productFromQueue);
                }
            } else {
                if (cheapest.size() == productsLimit) {
                    if (productFromCSV.compareTo(cheapest.peek()) > 0) {
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

    private static Product getMostExpensiveProductWithSameID(Product product, PriorityQueue<Product> products) {
        List<Product> productsWithSameID = products.stream()
                .filter(p -> p.getProductID() == product.getProductID())
                .sorted()
                .collect(Collectors.toList());
        return productsWithSameID.get(0);
    }

    private static File createCSV(PriorityQueue<Product> products) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("output.csv"));
        writer.writeNext(header);
        products.stream()
                .sorted(new ProductComparator())
                .forEach(p -> writer.writeNext(p.toStringArray()));
        writer.close();
        return new File("output.csv");
    }
}