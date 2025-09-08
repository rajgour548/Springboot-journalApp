package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    User findByUserName( String username);
    User deleteByUserName(String username);
    Optional<User> findByEmail(String email);



}
