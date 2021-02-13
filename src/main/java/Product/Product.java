package Product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Product implements Comparable<Product> {

    @Getter
    private int productID;
    private String name, condition, state;
    private float price;

    //Default incremental price comparison
    @Override
    public int compareTo(Product p) {
        return Float.compare(this.price, p.price);
    }

    public String[] toStringArray() {
        return String.format("%s,%s,%s,%s,%s", productID, name, condition, state, price).split(",");
    }
}
