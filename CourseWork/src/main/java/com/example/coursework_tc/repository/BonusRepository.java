package com.example.coursework_tc.repository;

import com.example.coursework_tc.model.Bonus;
import com.example.coursework_tc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long> {
    List<Bonus> findByUser(User user);

    // Найти премии пользователя за период
    List<Bonus> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}