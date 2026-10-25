
package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.dto.MoodAnalysisDTO;
import net.engineeringdigest.journalApp.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test/gemini")
public class GeminiTestController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<?> testGemini(
            @RequestBody Map<String, String> request) {

        String journalText = request.get("text");

        try {
            MoodAnalysisDTO analysis = geminiService.analyzeJournal(journalText);

            Map<String, Object> response = new HashMap<String, Object>();
            response.put("analysis", analysis);

            return ResponseEntity.ok(response);

        }  catch (Exception e) {
        // Print the real error in your backend console for debugging.
        e.printStackTrace();

        Map<String, String> response = new HashMap<>();
        response.put("error", "Gemini analysis failed");

        return ResponseEntity.status(502).body(response);
    }
    }
}
