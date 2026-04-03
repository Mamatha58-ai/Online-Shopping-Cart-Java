public class Product {
    public int id;
    public String name;
    public double price;
    public String imagePath;
    public int stock;

    public Product(int id, String name, double price, String imagePath, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.stock = stock;
    }

    public Product(String name, double price, String imagePath, int stock) {
        this(0, name, price, imagePath, stock);
    }

    public String toString() {
        return name + " - Rs. " + String.format("%.2f", price);
    }
}
