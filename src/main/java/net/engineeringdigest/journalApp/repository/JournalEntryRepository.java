package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.entities.JournalEntry;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

}
