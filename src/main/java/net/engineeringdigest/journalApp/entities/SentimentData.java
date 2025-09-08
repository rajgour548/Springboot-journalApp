package net.engineeringdigest.journalApp.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentimentData {
    private String actualName;
    private String email;
    private String sentiment;
}
