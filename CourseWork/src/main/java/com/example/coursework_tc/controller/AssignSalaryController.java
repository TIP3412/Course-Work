package com.example.coursework_tc.controller;

import com.example.coursework_tc.model.Bonus;
import com.example.coursework_tc.service.Impl.BonusService;
import com.example.coursework_tc.utils.ListUtils;
import com.example.coursework_tc.model.User;
import com.example.coursework_tc.model.WorkSchedule;
import com.example.coursework_tc.service.Impl.WorkScheduleService;
import com.example.coursework_tc.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users/{id}/assignSalary")
public class AssignSalaryController {

    @Autowired
    private UserService userService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private BonusService bonusService;

    // Отображение календаря
    @GetMapping
    public String showAssignSalaryPage(
            @PathVariable Long id,
            @RequestParam(required = false) String month,
            Model model,
            Authentication authentication) {

        User user = userService.getUserById(id);

        YearMonth selectedYearMonth = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.now();

        LocalDate startDate = selectedYearMonth.atDay(1);
        LocalDate endDate = selectedYearMonth.atEndOfMonth();

        List<WorkSchedule> workSchedules = workScheduleService.getWorkScheduleForUserAndMonth(user, startDate, endDate);
        Double totalEarnedAmount = workScheduleService.getTotalEarnedAmountForMonth(user, startDate, endDate);

        List<Bonus> bonuses = bonusService.getBonusesByUserAndMonth(user, startDate, endDate);
        Double totalBonusAmount = bonusService.getTotalBonusAmountForUserAndMonth(user, startDate, endDate);

        Double totalAmountWithBonus = totalEarnedAmount + totalBonusAmount;

        List<LocalDate> datesInMonth = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());

        int startOffset = startDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (startOffset < 0) {
            startOffset += 7;
        }

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> currentWeek = new ArrayList<>();

        for (int i = 0; i < startOffset; i++) {
            currentWeek.add(null);
        }

        for (LocalDate date : datesInMonth) {
            currentWeek.add(date);
            if (currentWeek.size() == 7) {
                weeks.add(currentWeek);
                currentWeek = new ArrayList<>();
            }
        }

        if (!currentWeek.isEmpty()) {
            while (currentWeek.size() < 7) {
                currentWeek.add(null);
            }
            weeks.add(currentWeek);
        }

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("user", user);
        model.addAttribute("workSchedules", workSchedules);
        model.addAttribute("totalEarnedAmount", totalEarnedAmount);
        model.addAttribute("totalAmountWithBonus", totalAmountWithBonus);
        model.addAttribute("bonuses", bonuses);
        model.addAttribute("totalBonusAmount", totalBonusAmount);
        model.addAttribute("selectedMonth", selectedYearMonth.toString());
        model.addAttribute("availableMonths", getAvailableMonths(user));
        model.addAttribute("weeks", weeks);
        model.addAttribute("isAdmin", isAdmin);

        return "assign_salary";
    }
    // Назначение премии
    @PostMapping("/assignBonus")
    public String assignBonus(
            @PathVariable Long id,
            @RequestParam Double bonusAmount,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            User user = userService.getUserById(id);

            Bonus bonus = new Bonus();
            bonus.setAmount(bonusAmount);
            bonus.setUser(user);
            bonus.setDate(LocalDate.now());

            bonusService.saveBonus(bonus);

            redirectAttributes.addFlashAttribute("bonusMessage", "Премия успешно назначена!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав для назначения премии.");
        }

        return "redirect:/users/{id}/assignSalary";
    }

    // Редакция календаря
    @PostMapping
    public String updateWorkSchedule(
            @PathVariable Long id,
            @RequestParam LocalDate workDate,
            @RequestParam Double hoursWorked,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            User user = userService.getUserById(id);
            workScheduleService.saveOrUpdateWorkSchedule(user, workDate, hoursWorked);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав для редактирования часов.");
        }

        return "redirect:/users/{id}/assignSalary";
    }

    // Список доступных месяцев (в Бд)
    private List<String> getAvailableMonths(User user) {
        List<String> months = new ArrayList<>();

        LocalDate minDate = workScheduleService.getMinWorkDateForUser(user);
        LocalDate maxDate = workScheduleService.getMaxWorkDateForUser(user);

        if (minDate != null && maxDate != null) {
            YearMonth start = YearMonth.from(minDate);
            YearMonth end = YearMonth.from(maxDate);

            while (!start.isAfter(end)) {
                months.add(start.toString());
                start = start.plusMonths(1);
            }
        }

        return months;
    }
}