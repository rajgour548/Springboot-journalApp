package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.Cache.AppCache;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AppCache appCache;
    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city){
        WeatherResponse weatherResponse = redisService.get("Weather_of_" + city, WeatherResponse.class);

       if(weatherResponse != null){
           return weatherResponse;
       }
       else{
           String finalApi =  appCache.appCache.get("WEATHER_API").replace("<apiKey>",apiKey).replace("<city>",city);
           ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET,null, WeatherResponse.class);
           WeatherResponse body = response.getBody();
           if(body != null){
               redisService.set("Weather_of_"+city,body,300l);
           }
           return body;
       }

    }
}
