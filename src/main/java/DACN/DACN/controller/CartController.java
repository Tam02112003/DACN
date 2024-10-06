package DACN.DACN.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    @GetMapping
    public String showCart(Model model) {

        return "/cart/cart";
    }
}
