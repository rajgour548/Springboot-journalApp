package net.engineeringdigest.journalApp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import net.engineeringdigest.journalApp.entities.User;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl {
    @Autowired
    private MongoTemplate mongoTemplate;



    public List<User> getUserForSA() {
        Query query = new Query();
        query.addCriteria(Criteria.where("email")
                .regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }


}
