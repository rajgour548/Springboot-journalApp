package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Disabled
    @Test
    public void testFindByUserName() {
        assertEquals(4, 2 + 2);
        assertNotNull(userRepository.findByUserName("raj"));
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,3",
            "1,1,2",
            "1,2,3"
    })
    public void test(int a,int b,int expected){
        assertEquals(expected,a+b);
    }

// important things that can help if u,re back to learn junit jupiter
//    1.-> you can use plugin (code coverage for java) that show how much code is tested yet
//    2.-> @BeforeEach
//          void setup() {
//                      // body
//                       }->runs before each test
//         @BeforeAll   -> runs before all test
//    @AfterEach -> runs after each test
//    @AfterAll  -> runs after all test

}
