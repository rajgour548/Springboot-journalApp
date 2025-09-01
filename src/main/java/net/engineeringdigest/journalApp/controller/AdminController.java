package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.Cache.AppCache;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name="Admin Apis")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private AppCache appCache;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
    List<User> user = userService.getAll();
    if(user.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/create-admin")
    public void createUser(@RequestBody User user){
        userService.saveNewAdmin( user);
    }

    @GetMapping("/clear-app-cache")
    public void clearAppCache(){
        appCache.init();
    }
}
