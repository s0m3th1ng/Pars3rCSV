public class Product implements Comparable<Product> {
    private int productID;
    private String name, condition, state;
    private float price;

    public Product(int productID, String name, String condition, String state, float price) {
        this.productID = productID;
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.price = price;
    }

    public int getProductID() {
        return productID;
    }

    public float getPrice() {
        return price;
    }

    //Default incremental price comparison
    @Override
    public int compareTo(Product p) {
        return Float.compare(this.price, p.price);
    }

    @Override
    public String toString() {
        return String.format("id: %s, name: %s, condition: %s, state: %s, price: %s", productID, name, condition, state, price);
    }

    public boolean isCheaper(Product p) {
        return this.compareTo(p) < 0;
    }

    public String[] toStringArray() {
        return String.format("%s,%s,%s,%s,%s", productID, name, condition, state, price).split(",");
    }
}
