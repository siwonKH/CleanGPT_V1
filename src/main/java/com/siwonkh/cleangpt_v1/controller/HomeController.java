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

    @GetMapping("/comment")
    public String searchComment(Model model) {
        return "searchVideoComments";
    }
}
//https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=9gC4BLcHMGM&key=AIzaSyDfiUY73P-WKoXBlyUxuZJUfvVBDswfdkw
