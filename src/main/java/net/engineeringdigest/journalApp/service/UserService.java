package net.engineeringdigest.journalApp.service;



import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.exception.ResourceNotFoundException;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrevoEmailService brevoEmailService;

    //instead use slf4j annotation on class//private static final Logger log = LoggerFactory.getLogger(UserService.class);

 private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUser(User user) {

        userRepository.save(user);
    }
    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        user.setAuthProvider("LOCAL");
        userRepository.save(user);
    }

    public void saveNewAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        user.setAuthProvider("LOCAL");
        userRepository.save(user);
    }

    public List<User> getAll (){
            List<User> user= userRepository.findAll();
            return user;
        }

    public User  findByUserName(String username){
        return userRepository.findByUserName(username);
    }

    public void  deleteByUserName(String username){
        long deletedCount = userRepository.deleteByUserName(username);

        if (deletedCount == 0) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public String recoverUsername(String email) {
        String genericMessage =
                "If an account exists for this email, you will receive further instructions at this email address within 5 minutes.";

        User user = userRepository.findByEmail(email).orElse(null);

        // Unknown email: return the generic message.
        if (user == null) {
            return genericMessage;
        }

        // Google account: send Google sign-in instructions.
        if ("GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {
            brevoEmailService.sendEmail(
                    email,
                    "Sign in to your JournalApp account",
                    "Your JournalApp account uses Google sign-in. "
                            + "Please sign in using Google."
            );

            return genericMessage;
        }

        // Local account: send the username.
        brevoEmailService.sendEmail(
                email,
                "Your JournalApp username",
                "Your JournalApp username is: " + user.getUserName()
        );

        return genericMessage;
    }


}
