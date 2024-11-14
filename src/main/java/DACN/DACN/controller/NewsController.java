package DACN.DACN.controller;

import DACN.DACN.entity.News;
import DACN.DACN.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/news")
@Controller
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping
    public String showNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Model model) {

            Pageable pageable = PageRequest.of(page, size);
            Page<News> newsPage = newsService.getAllNews(pageable);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Định dạng ngày cho mỗi bài viết
            for (News news : newsPage.getContent()) {
                if (news.getCreatedAt() != null) {
                    news.setFormattedCreatedAt(news.getCreatedAt().format(formatter));
                }
            }
        model.addAttribute("newsPage", newsPage);
        return "/admins/news/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("news", new News());
        return "/admins/news/create";
    }

    @PostMapping("/create")
    public String addNews(@ModelAttribute News news, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/admins/news/create";
        }

        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                news.setImgUrl("/img/" + imageName);  // Cập nhật đường dẫn hình ảnh
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        news.setCreatedAt(LocalDateTime.now());
        newsService.saveNews(news);
        return "redirect:/news";
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path dirImages = Paths.get("target/classes/static/img");
        if (!Files.exists(dirImages)) {
            Files.createDirectories(dirImages);
        }

        String newFileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());

        Path pathFileUpload = dirImages.resolve(newFileName);
        Files.copy(image.getInputStream(), pathFileUpload,
                StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        News news = newsService.getNewsById(id);
        // Kiểm tra xem bài viết có tồn tại không
        if (news != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng ngày
            news.setFormattedCreatedAt(news.getCreatedAt().format(formatter)); // Định dạng thời gian
        }
        if (news == null) {
            // Nếu không tìm thấy tin tức, chuyển hướng về danh sách
            return "redirect:/news";
        }
        model.addAttribute("news", news);
        return "/admins/news/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateNews(@PathVariable Long id, @ModelAttribute News news, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            news.setId(id);
            return "/admins/news/edit";
        }

        News existingNews = newsService.getNewsById(id);
        if (existingNews == null) {
            // Nếu không tìm thấy tin tức để cập nhật, chuyển hướng về danh sách
            return "redirect:/news";
        }
        news.setCreatedAt(LocalDateTime.now());

        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                news.setImgUrl("/img/" + imageName); // Sửa lại tên trường
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý lỗi nếu cần
            }
        } else {
            // Nếu không có hình ảnh mới, giữ nguyên hình ảnh cũ
            news.setImgUrl(existingNews.getImgUrl());
        }

        news.setId(id); // Gán ID cho tin tức
        newsService.saveNews(news);
        return "redirect:/news";
    }

    @GetMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return "redirect:/news";
    }

}