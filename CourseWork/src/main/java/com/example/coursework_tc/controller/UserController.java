package com.example.coursework_tc.controller;

import com.example.coursework_tc.model.User;
import com.example.coursework_tc.service.Impl.WorkScheduleService;
import com.example.coursework_tc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WorkScheduleService workScheduleService;
    // Перенаправление на регистрацию
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    // Регистрация
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/login";
    }
    // Просмотр списка сотрудников
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }
    // Личный кабинет сотрудника
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user_details";
    }
    // Переход на форму редактирвоания сотрудника
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        if (authentication != null) {
            model.addAttribute("authentication", authentication);
        }
        return "edit_user";
    }
    // Редактирование сотрудника
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User updatedUser, Authentication authentication) {
        User existingUser = userService.getUserById(id);
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setDepartment(updatedUser.getDepartment());
        existingUser.setDivision(updatedUser.getDivision());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            existingUser.setRole(updatedUser.getRole());
            existingUser.setSalary(updatedUser.getSalary());
        }

        userService.saveUser(existingUser);

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/users";
        } else {
            return "redirect:/users/" + id;
        }
    }
    // Удаление пользователя
    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
    // Сохранение премии сотрудника
    @PostMapping("/users/{id}/assignBonus")
    public String assignBonus(@PathVariable Long id,
                              @RequestParam Double bonusAmount,
                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bonus", bonusAmount);
        return "redirect:/users/{id}/assignSalary";
    }
    // Просмотр статистики сотрудника
    @GetMapping("/users/{id}/statistics")
    public String showStatistics(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        Map<String, Double> monthlySalary = workScheduleService.getMonthlySalaryStatistics(user);
        model.addAttribute("user", user);
        model.addAttribute("monthlySalary", monthlySalary);
        return "statistics";
    }
    // Просмотр статистики администартора (по депортаментам)
    @GetMapping("/admin/statistics")
    public String showAdminStatistics(Model model, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Map<String, Double> departmentSalary = userService.getDepartmentSalaryStatistics();
            model.addAttribute("departmentSalary", departmentSalary);
            return "admin_statistics";
        } else {
            return "redirect:/access-denied";
        }
    }
    // Вызов страницы "Об авторе"
    @GetMapping("/hello")
    public String hello(Model model, @RequestParam String role, @RequestParam String name) {
        model.addAttribute("role", role);
        model.addAttribute("name", name);
        return "hello";
    }
}