package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.dto.LoginRequestDto;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.service.UserService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name="Public Apis")
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signUP(@RequestBody net.engineeringdigest.journalApp.dto.UserDto user) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
try {
    userService.saveNewUser(newUser);
    Map<String, String> response = new HashMap<>();
    response.put("message", "Signup successful 🎉");
    return ResponseEntity.ok(response);
} catch (Exception e) {
    Map<String, String> response = new HashMap<>();
    response.put("error","user already exists");
    return ResponseEntity.badRequest().body(response);
}
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginInfo) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginInfo.getUserName(), loginInfo.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginInfo.getUserName());
            String jwt = jwtUtils.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
         log.error("Exception occured while generating token",e);
         return new ResponseEntity<>("Incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }
}

