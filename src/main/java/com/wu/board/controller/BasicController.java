package com.wu.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/")
    String index(Model model){
        model.addAttribute("activeMenu", "home");
        return "index";
    }
}
