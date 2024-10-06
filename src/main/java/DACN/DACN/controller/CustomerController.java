package DACN.DACN.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
    @RequiredArgsConstructor

    public class CustomerController {

        @GetMapping("/home")
        public String showSanphamList(Model model) {
           
            return "/customers/index";
        }

}
