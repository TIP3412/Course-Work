package com.example.coursework_tc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Перенаправление на авторизацию
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    // Перенаправление на блок Об Авторе
    @GetMapping("/about-author")
    public String aboutAuthor() {
        return "about_author";
    }
}
