package net.engineeringdigest.journalApp.entities;

import lombok.*;

import net.engineeringdigest.journalApp.Enum.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="journal_entries")
@Data
@NoArgsConstructor
/*
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@XSlf4j
*/

public class JournalEntry {
    @Id
    private ObjectId id;
    private String title;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;


}
