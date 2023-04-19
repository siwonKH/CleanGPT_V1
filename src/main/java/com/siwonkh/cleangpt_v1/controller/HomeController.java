package com.siwonkh.cleangpt_v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/comments")
    public String comments(Model model) {
        return "searchVideoComments";
    }
}
