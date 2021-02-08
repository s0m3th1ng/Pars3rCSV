import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {

    //Decremental price comparison
    @Override
    public int compare(Product p1, Product p2) {
        return p2.compareTo(p1);
    }
}
