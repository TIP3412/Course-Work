package com.example.coursework_tc.repository;

import com.example.coursework_tc.model.User;
import com.example.coursework_tc.model.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByUser(User user);

    // Найти график работы пользователя за период
    List<WorkSchedule> findByUserAndWorkDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // Найти график работы по пользователю и дате
    WorkSchedule findByUserAndWorkDate(User user, LocalDate workDate);

    // Найти минимальную дату работы пользователя
    @Query("SELECT MIN(ws.workDate) FROM WorkSchedule ws WHERE ws.user = :user")
    LocalDate findMinWorkDateByUser(@Param("user") User user);

    // Найти максимальную дату работы пользователя
    @Query("SELECT MAX(ws.workDate) FROM WorkSchedule ws WHERE ws.user = :user")
    LocalDate findMaxWorkDateByUser(@Param("user") User user);
}