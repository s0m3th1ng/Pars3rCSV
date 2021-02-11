import java.io.File;
import java.util.PriorityQueue;

public interface IDataCollector {

    PriorityQueue<Product> getSortedProducts(File file);
    Product getMostExpensiveProductWithSameID(Product product, PriorityQueue<Product> products);

}
