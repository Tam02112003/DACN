package DACN.DACN.services;

import DACN.DACN.entity.CartItem;
import DACN.DACN.entity.Product;
import DACN.DACN.entity.Size;
import DACN.DACN.repository.ProductRepository;
import DACN.DACN.repository.SizeRepository;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private List<CartItem> cartItems = new ArrayList<>();
    // Phương thức để lấy danh sách các mặt hàng trong giỏ hàng
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SizeRepository sizeRepository;

        // Lưu giỏ hàng trong session
        public void addToCart(Long productId, int quantity, Long sizeId, HttpSession session) {
            // Lấy giỏ hàng từ session
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }

            // Tìm kích cỡ theo ID
            Size size = sizeRepository.findById(sizeId)
                    .orElseThrow(() -> new IllegalArgumentException("Size không tồn tại: " + sizeId));

            // Kiểm tra xem sản phẩm có tồn tại trong giỏ hàng không
            CartItem existingItem = cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId) && item.getSize().equals(size))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;

                if (newQuantity <= 0) {
                    // Nếu số lượng <= 0, xóa sản phẩm khỏi giỏ hàng
                    removeFromCart(productId, sizeId, session);
                } else {
                    // Cập nhật số lượng mới
                    existingItem.setQuantity(newQuantity);
                }
            } else if (quantity > 0) {
                // Nếu sản phẩm chưa tồn tại và số lượng lớn hơn 0, thêm sản phẩm mới vào giỏ hàng
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
                cartItems.add(new CartItem(product, quantity, size));
            }

            // Cập nhật lại giỏ hàng trong session
            session.setAttribute("cart", cartItems);
        }

        // Phương thức xóa sản phẩm khỏi giỏ hàng
        public void removeFromCart(Long productId, Long sizeId, HttpSession session) {
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
            if (cartItems != null) {
                cartItems.removeIf(item -> item.getProduct().getId().equals(productId) && item.getSize().getId().equals(sizeId));
                session.setAttribute("cart", cartItems);
            }
        }

    public void clearCart() {
        cartItems.clear();
    }
    public void clearCart(HttpSession session) {
        session.removeAttribute("cart"); // Xóa giỏ hàng khỏi session
    }

    public void updateQuantity(Long productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
    }
    public void updateQuantity(Long productId, int quantity, HttpSession session) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.getProduct().getId().equals(productId)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
            // Cập nhật lại giỏ hàng vào session
            session.setAttribute("cart", cartItems);
        }
    }
    public double getTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
    public double getTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}