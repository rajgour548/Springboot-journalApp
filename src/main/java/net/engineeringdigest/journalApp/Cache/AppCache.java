package net.engineeringdigest.journalApp.Cache;

import net.engineeringdigest.journalApp.entities.JournalAppConfigEntity;
import net.engineeringdigest.journalApp.repository.JournalAppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class AppCache {
    public enum Keys{
        WEATHER_API;
    }

    @Autowired
    private JournalAppConfigRepository journalAppConfigRepository;
    public Map<String,String> appCache;

    @PostConstruct
    public void init(){
    appCache=new HashMap<>();
  List<JournalAppConfigEntity> all =  journalAppConfigRepository.findAll();
  for(JournalAppConfigEntity journalAppConfigEntity : all){
      appCache.put(journalAppConfigEntity.getKey(),journalAppConfigEntity.getValue());
  }
    }
}
