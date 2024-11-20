package DACN.DACN.controller;


import DACN.DACN.entity.CartItem;
import DACN.DACN.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @GetMapping
    public String showCart(Model model, HttpSession session) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");

        // Nếu giỏ hàng null, khởi tạo một danh sách rỗng
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", cartService.getTotalPrice(cartItems));
        return "/cart/cart";
    }
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            @RequestParam("sizeId") Long sizeId,
                            HttpSession session) {
        cartService.addToCart(productId, quantity, sizeId, session);
        return "redirect:/cart";
    }


    @PostMapping("/remove/{productId}/{sizeId}")
    public String removeFromCart(@PathVariable Long productId,
                                 @PathVariable Long sizeId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(productId, sizeId, session);
        redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/cart";
    }
    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Double> updateQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn không.");
        }

        cartService.updateQuantity(productId, quantity);

        // Lấy giá mới cho mặt hàng đã cập nhật
        double newPrice = cartService.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> item.getProduct().getPrice() * item.getQuantity())
                .orElse(0.0);

        // Tính tổng giá của tất cả mặt hàng trong giỏ
        double totalPrice = cartService.getTotalPrice();

        Map<String, Double> response = new HashMap<>();
        response.put("newPrice", newPrice);
        response.put("totalPrice", totalPrice);
        return response;
    }
    // Phương thức để tăng số lượng sản phẩm
    /*@PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId) {
        cartService.updateQuantity(productId, cartService.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> item.getQuantity() + 1)
                .orElse(1)); // Tăng số lượng lên 1 hoặc khởi tạo nếu chưa có
        return "redirect:/cart";
    }*/
    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId, HttpSession session) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Tìm sản phẩm trong giỏ hàng
        CartItem item = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            // Tăng số lượng lên 1
            cartService.updateQuantity(productId, item.getQuantity() + 1, session);
        }
        // Cập nhật giỏ hàng vào session
        session.setAttribute("cart", cartItems);
        return "redirect:/cart"; // Chuyển đến trang giỏ hàng
    }

    // Phương thức để giảm số lượng sản phẩm
    /*@PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId) {
        cartService.updateQuantity(productId, cartService.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> Math.max(item.getQuantity() - 1, 0)) // Giảm số lượng xuống 1, không để âm
                .orElse(0)); // Nếu chưa có thì số lượng là 0
        return "redirect:/cart";
    }*/
    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId, HttpSession session) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Tìm sản phẩm trong giỏ hàng
        CartItem item = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            // Giảm số lượng xuống 1, không để âm
            int newQuantity = Math.max(item.getQuantity() - 1, 0);
            item.setQuantity(newQuantity);
        }

        // Cập nhật giỏ hàng vào session
        session.setAttribute("cart", cartItems);
        return "redirect:/cart"; // Chuyển đến trang giỏ hàng
    }
}