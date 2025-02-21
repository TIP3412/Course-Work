package com.example.coursework_tc.service.Impl;

import com.example.coursework_tc.model.Bonus;
import com.example.coursework_tc.model.User;
import com.example.coursework_tc.repository.BonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BonusService {

    @Autowired
    private BonusRepository bonusRepository;

    // Сохранение премии
    public void saveBonus(Bonus bonus) {
        bonus.setDate(LocalDate.now());
        bonusRepository.save(bonus);
    }

    // Получение всех премий для пользователя за период
    public List<Bonus> getBonusesByUserAndMonth(User user, LocalDate startDate, LocalDate endDate) {
        return bonusRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    // Получение общей суммы премий для пользователя за период
    public Double getTotalBonusAmountForUserAndMonth(User user, LocalDate startDate, LocalDate endDate) {
        List<Bonus> bonuses = getBonusesByUserAndMonth(user, startDate, endDate);
        return bonuses.stream().mapToDouble(Bonus::getAmount).sum();
    }
}