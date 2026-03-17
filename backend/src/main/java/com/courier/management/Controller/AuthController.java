package com.courier.management.Controller;

import java.util.Map;

import com.courier.management.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.courier.management.entity.User;


@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserDetailsService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {

        String username = req.get("username");
        String password = req.get("password");

        User user = authService.login(username, password);

        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "clientId", user.getClient().getClientId(),
                "clientName", user.getClient().getClientName()
        ));
    }
}