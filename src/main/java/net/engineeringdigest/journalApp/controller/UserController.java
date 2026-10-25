package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.dto.DashboardStatsDTO;
import net.engineeringdigest.journalApp.dto.HomeResponseDto;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.DashboardService;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;



@RestController
@RequestMapping("/user")
@Tag(name="User Apis",description = "To get, update and delete user - for admin purpose")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private DashboardService dashboardService;

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

        // 🔹 Build the HomeResponseDto as usual
        HomeResponseDto response = new HomeResponseDto();
        response.setActualName(userInDb.getActualName());
        response.setEmail(userInDb.getEmail());
        response.setGreeting("Hi " + username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        DashboardStatsDTO stats =
                dashboardService.getDashboardStats(username);

        return ResponseEntity.ok(stats);
    }

}