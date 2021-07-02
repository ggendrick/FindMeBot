package com.ggenrick.fndmebot.data;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Document(collection = "cityProfiles")
public class CityProfile {
    @Id
    private int id;
    private String region;
    private String city;
}
