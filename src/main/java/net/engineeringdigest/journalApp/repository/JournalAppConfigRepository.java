package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.entities.JournalAppConfigEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalAppConfigRepository extends MongoRepository<JournalAppConfigEntity, ObjectId> {
}
