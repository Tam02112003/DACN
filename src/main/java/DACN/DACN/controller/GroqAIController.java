package DACN.DACN.controller;
import DACN.DACN.entity.Product;
import DACN.DACN.services.GroqAIService;
import DACN.DACN.services.ProductAIService;
import DACN.DACN.utills.KeywordExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class GroqAIController {

    private final GroqAIService aiService;
    private final ProductAIService productAIService;
    public GroqAIController(GroqAIService aiService, ProductAIService productAIService) {
        this.aiService = aiService;
        this.productAIService = productAIService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatEndpoint(@RequestBody Map<String, String> requestBody) {
        try {
            String userInput = requestBody.get("message");
            if (userInput == null || userInput.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "Message cannot be empty"));
            }

            // Trích xuất keyword chính từ tin nhắn
            String keyword = KeywordExtractor.extractKeyword(userInput);
            if (!keyword.isEmpty()) {
                List<Product> matchingProducts = productAIService.searchProducts(keyword);

                if (!matchingProducts.isEmpty()) {
                    StringBuilder response = new StringBuilder("FANFOOTBALL có các sản phẩm sau phù hợp với tìm kiếm của bạn:<br>");
                    for (Product p : matchingProducts) {
                        String imageHtml = p.getImgUrl() != null && !p.getImgUrl().isEmpty()
                                ? "<img src='" + p.getImgUrl() + "' style='max-width:100px; max-height:100px; margin-right:10px; vertical-align:middle;'/>"
                                : "";

                        response.append("<div style='margin-bottom:10px;'>")
                                .append(imageHtml)
                                .append("<span style='vertical-align:middle;'>")
                                .append(p.getName())
                                .append(" | Giá: ").append(p.getPrice()).append(" VNĐ")
                                .append("</span>")
                                .append("</div>");
                    }
                    return ResponseEntity.ok(Collections.singletonMap("response", response.toString()));
                }
            }

            // fallback nếu không có keyword phù hợp
            String aiResponse = aiService.chatWithAI(userInput);
            return ResponseEntity.ok(Collections.singletonMap("response", aiResponse));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }




}
