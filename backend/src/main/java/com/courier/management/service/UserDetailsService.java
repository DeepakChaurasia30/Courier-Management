package com.courier.management.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.courier.management.entity.User;
import com.courier.management.repository.UserRepository;

@Service
public class UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    public User login(String username, String password) {

        Optional<User> user = userRepo.findByUsernameAndPassword(username, password);

        return user.orElse(null);
    }
}