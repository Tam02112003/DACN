package DACN.DACN.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
    private Product product;
    private int quantity;
    private Size size;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Constructors
    public CartItem(Product product, int quantity, Size size) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
    }
    
}
