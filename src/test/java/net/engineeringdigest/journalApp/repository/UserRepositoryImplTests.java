package net.engineeringdigest.journalApp.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import net.engineeringdigest.journalApp.entities.User;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

@SpringBootTest
public class UserRepositoryImplTests {

    @Autowired
   private UserRepositoryImpl userRepositoryImpl;

    @Test
    public void getUserForSATest(){
  List<User> users = userRepositoryImpl.getUserForSA();
       Assertions.assertNotNull(users);

    }

}
