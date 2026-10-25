
package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.dto.JournalEntryRequest;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.exception.InvalidRequestException;
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
@Tag(name="Journal Entry Apis",description = "TO add, delete, update, get journal entries")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntryOfUsers() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        List<JournalEntry> journalEntries =
                journalEntryService.getAll(username);

        return ResponseEntity.ok(journalEntries);
    }
    @PostMapping
    public ResponseEntity<JournalEntry> createEntryOfUser(
            @RequestBody JournalEntryRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        JournalEntry savedEntry =
                journalEntryService.saveEntry(request, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedEntry);
    }
    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getById(
            @PathVariable String myId) {

        if (!ObjectId.isValid(myId)) {
            throw new InvalidRequestException("Invalid journal ID");
        }

        ObjectId objectId = new ObjectId(myId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        JournalEntry journalEntry =
                journalEntryService.getJournalEntryByIdAndUsername(
                        objectId, username);

        return ResponseEntity.ok(journalEntry);
    }


    @DeleteMapping("id/{myId}")
    public ResponseEntity<Void> deleteEntryById(
            @PathVariable String myId) {

        if (!ObjectId.isValid(myId)) {
            throw new InvalidRequestException("Invalid journal ID");
        }

        ObjectId objectId = new ObjectId(myId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        journalEntryService.deleteById(objectId, username);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<JournalEntry> updateEntryById(
            @PathVariable String myId,
            @RequestBody JournalEntryRequest request) {

        if (!ObjectId.isValid(myId)) {
            throw new InvalidRequestException("Invalid journal ID");
        }

        ObjectId objectId = new ObjectId(myId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        JournalEntry updatedEntry =
                journalEntryService.updateEntryById(
                        objectId, username, request);

        return ResponseEntity.ok(updatedEntry);
    }
}
