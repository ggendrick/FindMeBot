package com.ggenrick.fndmebot.utils;

import com.ggenrick.fndmebot.data.CityProfile;
import com.ggenrick.fndmebot.service.CityProfileDataService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;

@Service
public class CityProfilesUpdater {
    CityProfileDataService cityProfileDataService;
    private static final String PATH="C:/Users/evgen/IdeaProjects/FndMeBot/src/main/resources/json/russia.json";
    @Autowired
    CityProfilesUpdater(CityProfileDataService cityProfileDataService){
        this.cityProfileDataService=cityProfileDataService;
    }

    public void updateCities(){
        try {
            JsonReader reader= new JsonReader(new FileReader(PATH));
            CityProfile[] cityes=new Gson().fromJson(reader, CityProfile[].class);
            System.out.println(cityes.length);
            int id=0;

            for(CityProfile city:cityes){
               city.setId(id);
               cityProfileDataService.SaveCity(city);
               id++;
            }
        }catch (Exception e){
        }
    }
}
