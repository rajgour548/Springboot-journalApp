package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.dto.HomeResponseDto;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/user")
@Tag(name="User Apis",description = "To get, update and delete user - for admin purpose")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @PutMapping
    public void updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb = userService.findByUserName(username);
            userInDb.setUserName(userInDb.getUserName());
            userInDb.setActualName(user.getActualName());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);

    }

    @DeleteMapping
    public void deleteByUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
     userService.deleteByUserName(username);
    }

    @GetMapping("/home")
    public ResponseEntity<HomeResponseDto> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userInDb = userService.findByUserName(username);

        // 🔹 Creating a static WeatherResponse because i reached monthly calls for weather Api, once its restart i will remove this and use actual Api
        WeatherResponse weatherResponse = new WeatherResponse();
        WeatherResponse.Current current = weatherResponse.new Current();
        current.setFeelslike(28); // static feels like temperature
        current.setTemperature(30); // static temperature
        current.setWeatherDescriptions(Arrays.asList("Sunny with clear skies ☀️"));
        weatherResponse.setCurrent(current);

        // 🔹 Build the HomeResponseDto as usual
        HomeResponseDto response = new HomeResponseDto();
        response.setActualName(userInDb.getActualName());
        response.setEmail(userInDb.getEmail());
        response.setUserName(userInDb.getUserName());


        if (weatherResponse != null && weatherResponse.getCurrent() != null) {
            response.setFeelsLike(weatherResponse.getCurrent().getFeelslike());
            response.setWeatherDescriptions(weatherResponse.getCurrent().getWeatherDescriptions());
            response.setGreeting("The Temperature today feels like " + weatherResponse.getCurrent().getFeelslike() + "°C");
        } else {
            response.setGreeting("Hi " + username);
        }

        return ResponseEntity.ok(response);
    }



}