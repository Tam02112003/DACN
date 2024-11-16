package DACN.DACN.controller;

import DACN.DACN.entity.Brand;
import DACN.DACN.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Hiển thị danh sách thương hiệu
    @GetMapping
    public String getAllBrands(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        return "/admins/brand/list"; // Trả về view danh sách thương hiệu
    }

    @GetMapping("/create")
    public String createBrandForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "/admins/brand/create"; // Trả về view tạo thương hiệu
    }

    // Xử lý form tạo thương hiệu
    @PostMapping("/create")
    public String createBrand(@ModelAttribute Brand brand) {
        brandService.createBrand(brand);
        return "redirect:/brands"; // Chuyển hướng về danh sách thương hiệu
    }

    // Hiển thị form cập nhật thương hiệu
    @GetMapping("/edit/{id}")
    public String editBrandForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.getBrandById(id)
                .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
        model.addAttribute("brand", brand);
        return "/admins/brand/edit"; // Trả về view chỉnh sửa thương hiệu
    }

    // Xử lý form cập nhật thương hiệu
    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable Long id, @ModelAttribute Brand brandDetails) {
        brandService.updateBrand(id, brandDetails);
        return "redirect:/brands"; // Chuyển hướng về danh sách thương hiệu
    }

    // Xóa thương hiệu
    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return "redirect:/brands"; // Chuyển hướng về danh sách thương hiệu
    }
}