package com.example.coursework_tc.service.Impl;

import com.example.coursework_tc.model.Bonus;
import com.example.coursework_tc.model.User;
import com.example.coursework_tc.model.WorkSchedule;
import com.example.coursework_tc.repository.BonusRepository;
import com.example.coursework_tc.repository.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WorkScheduleService {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;
    @Autowired
    private BonusRepository bonusRepository;

    // Получить график работы за месяц
    public List<WorkSchedule> getWorkScheduleForUserAndMonth(User user, LocalDate startDate, LocalDate endDate) {
        return workScheduleRepository.findByUserAndWorkDateBetween(user, startDate, endDate);
    }

    // Получить минимальную дату работы пользователя
    public LocalDate getMinWorkDateForUser(User user) {
        return workScheduleRepository.findMinWorkDateByUser(user);
    }

    // Получить максимальную дату работы пользователя
    public LocalDate getMaxWorkDateForUser(User user) {
        return workScheduleRepository.findMaxWorkDateByUser(user);
    }

    // Сохранить или обновить календарь
    public WorkSchedule saveOrUpdateWorkSchedule(User user, LocalDate workDate, Double hoursWorked) {
        WorkSchedule workSchedule = workScheduleRepository.findByUserAndWorkDate(user, workDate);
        if (workSchedule == null) {
            workSchedule = new WorkSchedule();
            workSchedule.setUser(user);
            workSchedule.setWorkDate(workDate);
        }
        workSchedule.setHoursWorked(hoursWorked);
        workSchedule.setEarnedAmount(user.getSalary() * hoursWorked); // Рассчитываем заработок
        return workScheduleRepository.save(workSchedule);
    }

    // Получить общую сумму заработка за месяц
    public Double getTotalEarnedAmountForMonth(User user, LocalDate startDate, LocalDate endDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserAndWorkDateBetween(user, startDate, endDate);
        return schedules.stream().mapToDouble(WorkSchedule::getEarnedAmount).sum();
    }

    public Map<String, Double> getMonthlySalaryStatistics(User user) {
        // Получаем все записи о работе пользователя
        List<WorkSchedule> workSchedules = workScheduleRepository.findByUser(user);

        // Получаем все премии пользователя
        List<Bonus> bonuses = bonusRepository.findByUser(user);

        // Создаем карту для хранения суммы по месяцам
        Map<String, Double> monthlySalary = new TreeMap<>();

        // Добавляем заработанные суммы
        for (WorkSchedule schedule : workSchedules) {
            String yearMonth = YearMonth.from(schedule.getWorkDate()).toString(); // Формат "YYYY-MM"
            monthlySalary.put(yearMonth, monthlySalary.getOrDefault(yearMonth, 0.0) + schedule.getEarnedAmount());
        }

        // Добавляем премии
        for (Bonus bonus : bonuses) {
            String yearMonth = YearMonth.from(bonus.getDate()).toString(); // Формат "YYYY-MM"
            monthlySalary.put(yearMonth, monthlySalary.getOrDefault(yearMonth, 0.0) + bonus.getAmount());
        }

        return monthlySalary;
    }
}