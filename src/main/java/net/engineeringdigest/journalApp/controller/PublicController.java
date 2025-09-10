package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.engineeringdigest.journalApp.dto.LoginRequestDto;
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
import org.springframework.security.core.userdetails.UserDetails;
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
@Tag(name="Public Apis",description = "Include signup,login,send-code,verify")
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



    @PostMapping("/signup")
    public ResponseEntity<String> signUP(@Valid @RequestBody net.engineeringdigest.journalApp.dto.UserDto user) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setActualName(user.getActualName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
try {
    userService.saveNewUser(newUser);
    return new ResponseEntity<>(HttpStatus.OK);
} catch (Exception e) {
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
}
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginInfo) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginInfo.getUserName(),
                            loginInfo.getPassword()
                    )
            );

            User user = userService.findByUserName(loginInfo.getUserName());

            // Generate JWT directly
            String jwt = jwtUtils.generateToken(user.getUserName());

            Map<String, String> response = new HashMap<>();
            response.put("jwt", jwt);
            response.put("email", user.getEmail());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<?> verifyForgotPasswordCode(@RequestBody VerifyRequest request) {
        boolean valid = verificationService.verifyCode(request.getEmail(), request.getCode());
        if (!valid) {
            return new ResponseEntity<>("Invalid or expired code", HttpStatus.BAD_REQUEST);
        }
        User user = userService.findByEmail(request.getEmail());
        String jwt = jwtUtils.generateToken(user.getUserName()); // generate JWT after verification
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }



}

