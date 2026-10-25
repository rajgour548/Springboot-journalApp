
package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.dto.JournalEntryRequest;
import net.engineeringdigest.journalApp.dto.MoodAnalysisDTO;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.exception.ResourceNotFoundException;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GeminiService geminiService;

    @Transactional
    public JournalEntry saveEntry(JournalEntryRequest request, String username) {

        User user = getUserOrThrow(username);

        JournalEntry myentry = new JournalEntry();
        myentry.setTitle(request.getTitle());
        myentry.setContent(request.getContent());
        myentry.setDate(LocalDateTime.now());

        // Analyze the journal text using Gemini.
        try {
            MoodAnalysisDTO analysis =
                    geminiService.analyzeJournal(
                            myentry.getTitle() + "\n" + myentry.getContent()
                    );

            myentry.setSentiment(
                    net.engineeringdigest.journalApp.Enum.Sentiment
                            .valueOf(analysis.getSentiment())
            );

            myentry.setEmotions(analysis.getEmotions());

        } catch (Exception e) {
            // AI failure should not prevent saving the journal.
            System.err.println(
                    "Gemini mood analysis failed: " + e.getMessage()
            );
        }

        JournalEntry savedEntry =
                journalEntryRepository.save(myentry);

        user.getJournalEntries().add(savedEntry);
        userService.saveUser(user);

        return savedEntry;
    }


    public List<JournalEntry> getAll(String username) {

        User user = getUserOrThrow(username);

        if (user.getJournalEntries() == null) {
            return Collections.emptyList();
        }

        return user.getJournalEntries();
    }
    public JournalEntry getJournalEntryByIdAndUsername(
            ObjectId id, String username) {

        User user = getUserOrThrow(username);

        boolean belongsToUser =
                user.getJournalEntries() != null
                        && user.getJournalEntries().stream()
                        .anyMatch(entry -> entry.getId().equals(id));

        if (!belongsToUser) {
            throw new ResourceNotFoundException(
                    "Journal entry not found");
        }

        return journalEntryRepository.findById(String.valueOf(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Journal entry not found"));
    }

    @Transactional
    public void deleteById(ObjectId myId, String username) {

        User user = getUserOrThrow(username);

        if (user.getJournalEntries() == null) {
            throw new ResourceNotFoundException("Journal entry not found");
        }

        boolean check = user.getJournalEntries()
                .removeIf(x -> x.getId().equals(myId));

        if (!check) {
            throw new ResourceNotFoundException(
                    "Journal entry not found");
        }

        userService.saveUser(user);

        journalEntryRepository.deleteById(String.valueOf(myId));
    }
    public JournalEntry updateEntryById(
            ObjectId id, String username, JournalEntryRequest request) {

        User user = getUserOrThrow(username);

        boolean belongsToUser =
                user.getJournalEntries() != null
                        && user.getJournalEntries().stream()
                        .anyMatch(entry -> entry.getId().equals(id));

        if (!belongsToUser) {
            throw new ResourceNotFoundException(
                    "Journal entry not found");
        }

        JournalEntry oldEntry = journalEntryRepository.findById(String.valueOf(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Journal entry not found"));

        if (request.getTitle() != null
                && !request.getTitle().trim().isEmpty()) {
            oldEntry.setTitle(request.getTitle());
        }

        if (request.getContent() != null
                && !request.getContent().trim().isEmpty()) {
            oldEntry.setContent(request.getContent());
        }


        try {
            MoodAnalysisDTO analysis = geminiService.analyzeJournal(
                    oldEntry.getTitle() + "\n" + oldEntry.getContent()
            );

            oldEntry.setSentiment(
                    net.engineeringdigest.journalApp.Enum.Sentiment
                            .valueOf(analysis.getSentiment())
            );

            oldEntry.setEmotions(analysis.getEmotions());

        } catch (Exception e) {
            // Keep the existing analysis if Gemini fails.
            System.err.println(
                    "Gemini mood analysis failed during update: "
                            + e.getMessage()
            );
        }

        return journalEntryRepository.save(oldEntry);

    }
    private User getUserOrThrow(String username) {
        User user = userService.findByUserName(username);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

}
