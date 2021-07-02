package com.ggenrick.fndmebot.data;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "userProfile")
public class UserProfile {

    @Id
    private String id;
    private Long chatId;
    private String partnerGender;
    private String gender;
    private String username;
    private String name;
    private int age;
    private String info;
    private String photo;
    private List<Long> likedId;
    private int locationId;
}
