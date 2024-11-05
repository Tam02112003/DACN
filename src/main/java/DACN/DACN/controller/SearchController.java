package DACN.DACN.controller;

import DACN.DACN.entity.Product;
import DACN.DACN.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService;

    // Phương thức trả về kết quả tìm kiếm dưới dạng JSON
    @GetMapping("/search")
    @ResponseBody
    public List<Product> search(@RequestParam("query") String query) {
        return productService.getProducts(1, query, 10).getContent(); // Trả về sản phẩm phù hợp
    }

    // Phương thức trả về trang kết quả tìm kiếm
    @GetMapping("/search-results")
    public String searchResults(@RequestParam("query") String query,
                                @RequestParam(defaultValue = "1") int page,
                                Model model) {
        Page<Product> results = productService.getProducts(page, query, 10);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "searchResults"; // Trả về view searchResults
    }
    // Phương thức trả về gợi ý sản phẩm
    @GetMapping("/suggestions")
    public String getSuggestions(@RequestParam("query") String query, Model model) {
        Page<Product> suggestions = productService.getProductsByStartingLetter(query, 1, 10);
        model.addAttribute("suggestions", suggestions);
        return "suggestions"; // Trả về view để hiển thị gợi ý
    }
}