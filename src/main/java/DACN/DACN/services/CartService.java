package DACN.DACN.services;

import DACN.DACN.entity.CartItem;
import DACN.DACN.entity.Product;
import DACN.DACN.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    @Autowired
    private ProductRepository productRepository;
    public void addToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            // Nếu số lượng là 0, xóa sản phẩm khỏi giỏ hàng
            removeFromCart(productId);
            return;
        }

        // Tìm sản phẩm trong giỏ hàng
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Nếu sản phẩm chưa có, tìm sản phẩm từ repository và thêm mới
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
            cartItems.add(new CartItem(product, quantity));
        }
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    public void clearCart() {
        cartItems.clear();
    }
    public void updateQuantity(Long productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
    }
    public double getTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
