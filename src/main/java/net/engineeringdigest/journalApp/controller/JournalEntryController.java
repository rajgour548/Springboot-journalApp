package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("journal")
@Tag(name="Journal Entry Apis")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntryOfUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        List<JournalEntry> journalEntry =  user.getJournalEntries();
        if(journalEntry.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(journalEntry,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<JournalEntry> createEntryOfUser(@RequestBody JournalEntry myentry){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           String username = authentication.getName();
            journalEntryService.saveEntry(myentry,username);
            return new ResponseEntity<>(myentry,HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getById(@PathVariable String myId){
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
       User user = userService.findByUserName(username);
       List <JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(objectId)).collect(Collectors.toList());
       if(!collect.isEmpty()){
           Optional <JournalEntry > journalEntry = journalEntryService.getById(objectId);
           if(journalEntry.isPresent()){
               return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
           }
       }

       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable String myId){
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional <JournalEntry > journalEntry = journalEntryService.getById(objectId);
        if(journalEntry.isPresent()) {
            journalEntryService.deleteById(objectId,username);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("id/{myId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String myId, @RequestBody JournalEntry newentry){
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        List <JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional <JournalEntry > journalEntry = journalEntryService.getById(objectId);
                JournalEntry old = journalEntry.get();
                old.setTitle(newentry.getTitle() != null && !newentry.getTitle().equals("") ? newentry.getTitle():old.getTitle());
                old.setContent(newentry.getContent() !=null && !newentry.getContent().equals("")?newentry.getContent():old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old,HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
