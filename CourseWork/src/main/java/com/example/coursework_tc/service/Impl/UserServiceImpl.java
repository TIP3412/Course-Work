package com.example.coursework_tc.service.Impl;

import com.example.coursework_tc.model.User;
import com.example.coursework_tc.repository.UserRepository;
import com.example.coursework_tc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        if (user != null) {
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setDepartment(userDetails.getDepartment());
            user.setDivision(userDetails.getDivision());
            user.setAddress(userDetails.getAddress());
            user.setDateOfBirth(userDetails.getDateOfBirth());
            user.setRole(userDetails.getRole());
            user.setSalary(userDetails.getSalary());
            return userRepository.save(user);
        }
        return null;
    }
    // Статистика для админа
    public Map<String, Double> getDepartmentSalaryStatistics() {
        List<User> users = userRepository.findAll();

        Map<String, Double> departmentSalary = new HashMap<>();

        for (User user : users) {
            String department = user.getDepartment().toLowerCase();
            double salary = user.getSalary() != null ? user.getSalary() : 0.0;

            departmentSalary.put(department, departmentSalary.getOrDefault(department, 0.0) + salary);
        }

        return departmentSalary;
    }

    @Override
    public User assignSalary(Long id, Double salary) {
        return null;
    }
}