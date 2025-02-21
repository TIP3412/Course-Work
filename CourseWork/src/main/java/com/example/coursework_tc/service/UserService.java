package com.example.coursework_tc.service;

import com.example.coursework_tc.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserByEmail(String email);

    User saveUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

    User updateUser(Long id, User userDetails);

    User assignSalary(Long id, Double salary);

    Map<String, Double> getDepartmentSalaryStatistics();
}