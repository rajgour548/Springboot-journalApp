package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.internal.dynalink.support.NameCodec;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.engineeringdigest.journalApp.dto.LoginRequestDto;
import net.engineeringdigest.journalApp.dto.ResetPasswordRequest;
import net.engineeringdigest.journalApp.dto.VerifyRequest;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.VerificationService;
import net.engineeringdigest.journalApp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name="Public Apis",description = "Include signup,login,send-code,verify,forgot-username,reset-password")
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/signup")
    public ResponseEntity<String> signUP(@Valid @RequestBody net.engineeringdigest.journalApp.dto.UserDto user) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setActualName(user.getActualName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
        userService.saveNewUser(newUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDto loginInfo) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginInfo.getUserName(),
                                loginInfo.getPassword()
                        )
                );

        User user = userService.findByUserName(
                loginInfo.getUserName());

        String jwt = jwtUtils.generateToken(authentication);

        Map<String, String> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User userOpt = userService.findByEmail(email);
        if (userOpt==null) {
            return new ResponseEntity<>("Email not registered", HttpStatus.BAD_REQUEST);
        }
        try {
            verificationService.generateAndSendCode(email);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to send code: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Code sent", HttpStatus.OK);
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyForgotPasswordCode(
            @RequestBody VerifyRequest request) {

        boolean valid = verificationService.verifyCode(
                request.getEmail(), request.getCode());

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired code");
        }

        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired code");
        }

        String resetToken =
                jwtUtils.generatePasswordResetToken(user.getUserName());

        return ResponseEntity.ok(
                java.util.Collections.singletonMap("resetToken", resetToken));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        String username = jwtUtils.validatePasswordResetToken(request.getResetToken());

        if (username == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired password reset token");
        }

        if (request.getNewPassword() == null
                || request.getNewPassword().length() < 8) {
            return ResponseEntity.badRequest()
                    .body("Password must contain at least 8 characters");
        }

        User user = userService.findByUserName(username);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid password reset request");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.saveUser(user);

        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<Map<String, String>> forgotUsername(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String message = userService.recoverUsername(email.trim());

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }



}

