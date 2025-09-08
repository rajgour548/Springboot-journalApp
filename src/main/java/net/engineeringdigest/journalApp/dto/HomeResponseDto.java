package net.engineeringdigest.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponseDto {
        private String actualName;
        private String email;
        private String userName;
        private String password;
        private String greeting;
        private List<String> weatherDescriptions;
        private int feelsLike;
}
