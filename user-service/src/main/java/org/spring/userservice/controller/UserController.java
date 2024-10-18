package org.spring.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.spring.userservice.controller.request.ChangePasswordRequest;
import org.spring.userservice.model.User;
import org.spring.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) throws InvalidCredentialsException {
        String token = userService.login(user);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String header) {
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        boolean isValid = userService.validate(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) throws InvalidCredentialsException {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }
}
