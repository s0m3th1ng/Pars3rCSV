package FileCreator;

import Product.Product;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

public interface IFileCreator {

    File createFile(PriorityQueue<Product> products) throws IOException;

}
