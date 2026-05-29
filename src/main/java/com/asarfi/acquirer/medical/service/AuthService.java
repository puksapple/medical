package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.configuration.JwtService;
import com.asarfi.acquirer.medical.dto.UserDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final CompanyRepository companyRepository;

    public UserDto register(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(userDto.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Company company = new Company();
        company.setName(userDto.getCompanyName());
        company.setEmail(userDto.getCompanyEmail());
        company.setPhone(userDto.getCompanyPhone());
        company.setAddress(userDto.getCompanyAddress());
        company.setActive(true);

        Company savedCompany = companyRepository.save(company);

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setActive(false);
        user.setRole(role);
        user.setCompany(savedCompany);

        User savedUser = userRepository.save(user);

        String verificationTokenValue = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(verificationTokenValue);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(verificationToken);

        String verificationLink =
                "https://medical-production-2187.up.railway.app/api/auth/verify-email?token=" + verificationTokenValue;

        emailService.sendEmail(
                savedUser.getEmail(),
                "Verify your email",
                "Please verify your email by clicking this link: " + verificationLink
        );

        UserDto response = new UserDto();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.getActive());
        response.setRoleName(savedUser.getRole().getName());

        response.setCompanyId(savedCompany.getId());
        response.setCompanyName(savedCompany.getName());
        response.setCompanyEmail(savedCompany.getEmail());
        response.setCompanyPhone(savedCompany.getPhone());
        response.setCompanyAddress(savedCompany.getAddress());

        response.setToken(verificationTokenValue);

        return response;
    }

    public UserDto login(UserDto userDto) {

        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.getActive()) {
            throw new RuntimeException("Please verify your email before login");
        }

        String token = jwtService.generateToken(user);

        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setActive(user.getActive());
        response.setRoleName(user.getRole().getName());
        response.setToken(token);

        return response;
    }

    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(resetToken);

        return token;
    }

    public String resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);

        return "Password reset successful";
    }

    public String changePassword(String email, String oldPassword, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return "Password changed successfully";
    }

    public String verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token expired");
        }

        User user = verificationToken.getUser();
        user.setActive(true);

        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);

        return "Email verified successfully";
    }

    public UserDto getCurrentUser(String email) {


            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDto dto = new UserDto();

            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setActive(user.getActive());
            dto.setRoleName(user.getRole().getName());

            dto.setCompanyId(user.getCompany().getId());
            dto.setCompanyName(user.getCompany().getName());
            dto.setCompanyEmail(user.getCompany().getEmail());
            dto.setCompanyPhone(user.getCompany().getPhone());
            dto.setCompanyAddress(user.getCompany().getAddress());

            return dto;
        }
}