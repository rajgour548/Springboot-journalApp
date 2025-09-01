package net.engineeringdigest.journalApp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document("config_journal_app")
@Data
@NoArgsConstructor
public class JournalAppConfigEntity {

    public String key;
    public String value;

}
