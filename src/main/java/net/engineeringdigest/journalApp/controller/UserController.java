package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
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
import java.util.List;


@RestController
@RequestMapping("/user")
@Tag(name="User Apis",description = "Read Update and Delete user through this Api")
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
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);

    }

    @DeleteMapping
    public void deleteByUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
     userService.deleteByUserName(username);
    }

    @GetMapping
    public ResponseEntity<?> greeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WeatherResponse weatherResponse = weatherService.getWeather("Delhi");
        String greetings="";
        List<String> days = new ArrayList<String>();

        if(weatherResponse != null) {
           greetings = " Weather feels like " + weatherResponse.getCurrent().getFeelslike();
           days = weatherResponse.getCurrent().getWeatherDescriptions();
           for( String day : days){
               return new ResponseEntity<>("hii," + username + greetings+", "+day, HttpStatus.OK);
           }
        }
        return new ResponseEntity<>("hii," + username , HttpStatus.OK);
    }

}