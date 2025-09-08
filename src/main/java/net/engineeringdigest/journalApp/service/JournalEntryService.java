
package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;


    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.findByUserName(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occured while saving Entry"+e);
        }
    }
    public void saveEntry(JournalEntry journalEntry) {
        journalEntry.setDate(LocalDateTime.now());
        journalEntryRepository.save(journalEntry);
    }
    public List<JournalEntry> getAll (){
        List<JournalEntry> journalEntry = journalEntryRepository.findAll();
        return journalEntry;
    }

    public Optional<JournalEntry>  getById(ObjectId myId){
        return journalEntryRepository.findById(String.valueOf(myId));
    }
    @Transactional
    public void deleteById(ObjectId myId, String username){
        try{
            User user= userService.findByUserName(username);
            boolean check = user.getJournalEntries().removeIf(x -> x.getId().equals(myId));
            if(check) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(String.valueOf(myId));

            }
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occured while deleting Entry"+e);
        }
    }
}
