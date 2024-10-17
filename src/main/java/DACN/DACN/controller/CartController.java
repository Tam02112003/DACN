package DACN.DACN.controller;


import DACN.DACN.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "/cart/cart";
    }
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            @RequestParam("sizeId") Long sizeId) {
        cartService.addToCart(productId, quantity, sizeId);
        return "redirect:/cart";
    }


    @PostMapping("/remove/{productId}/{sizeId}")
    public String removeFromCart(@PathVariable Long productId,
                                 @PathVariable Long sizeId,
                                 RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(productId, sizeId);
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
    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId) {
        cartService.updateQuantity(productId, cartService.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> item.getQuantity() + 1)
                .orElse(1)); // Tăng số lượng lên 1 hoặc khởi tạo nếu chưa có
        return "redirect:/cart";
    }

    // Phương thức để giảm số lượng sản phẩm
    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId) {
        cartService.updateQuantity(productId, cartService.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> Math.max(item.getQuantity() - 1, 0)) // Giảm số lượng xuống 1, không để âm
                .orElse(0)); // Nếu chưa có thì số lượng là 0
        return "redirect:/cart";
    }
}
