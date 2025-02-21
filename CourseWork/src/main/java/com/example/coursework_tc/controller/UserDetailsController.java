package com.example.coursework_tc.controller;

import com.example.coursework_tc.model.User;
import com.example.coursework_tc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserDetailsController {

    @Autowired
    private UserService userService;

    // Метод для аунтефикации
    @GetMapping("/user_details")
    public String viewUserDetails(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("authentication", authentication);
        return "user_details";
    }
}
