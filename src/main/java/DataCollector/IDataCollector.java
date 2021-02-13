package DataCollector;

import Product.Product;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

public interface IDataCollector {

    PriorityQueue<Product> getSortedProducts(File file) throws IOException;

}
