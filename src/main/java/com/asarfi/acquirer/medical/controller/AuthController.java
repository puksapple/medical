package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.UserDto;
import com.asarfi.acquirer.medical.entity.User;
import com.asarfi.acquirer.medical.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserDto register(@RequestBody UserDto userDto) {
        return authService.register(userDto);
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody UserDto userDto) {
        return authService.login(userDto);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        return authService.resetPassword(token, newPassword);
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        return authService.changePassword(email, oldPassword, newPassword);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @GetMapping("/me")
    public UserDto me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return authService.getCurrentUser(user.getEmail());
    }



}