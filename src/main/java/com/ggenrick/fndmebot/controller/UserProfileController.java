package com.ggenrick.fndmebot.controller;

import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.CityProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserProfileController {
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, String> userProfileName = new HashMap<>();
    private Map<Long, Integer> userProfileAge = new HashMap<>();
    private Map<Long, String> userProfileGender = new HashMap<>();
    private Map<Long, String> userProfilePartnerGender = new HashMap<>();
    private Map<Long, String> userProfileInfo = new HashMap<>();
    private Map<Long, String> userProfilePhoto = new HashMap<>();
    private Map<Long, List<CityProfile>> userProfileLocationList = new HashMap<>();
    private Map<Long, Integer> userProfileLocationId = new HashMap<>();

    public void setUserProfileLocationId(long userId, Integer cityId) {
        userProfileLocationId.put(userId, cityId);
    }


    public int getUserProfileLocationId(long userId) {
        return userProfileLocationId.get(userId);
    }


    public void setUsersBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUserBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) botState = BotState.DEFAULT;
        return botState;
    }

    public void setUserProfileName(long userId, String name) {
        userProfileName.put(userId, name);
    }

    public void setUserProfileAge(long userId, int age) {
        userProfileAge.put(userId, age);
    }

    public void setUserProfileGender(long userId, String gender) {
        userProfileGender.put(userId, gender);
    }

    public void setUserProfilePartnerGender(long userId, String partnerGender) {
        userProfilePartnerGender.put(userId, partnerGender);
    }

    public void setUserProfileInfo(long userId, String info) {
        userProfileInfo.put(userId, info);
    }

    public void setUserProfilePhoto(long userId, String photo) {
        userProfilePhoto.put(userId, photo);
    }

    public void setUserProfileLocationList(long userId, List<CityProfile> cityList) {
        userProfileLocationList.put(userId, cityList);
    }

    public String getUserProfileName(long userId) {
        return userProfileName.get(userId);
    }

    public int getUserProfileAge(long userId) {
        return userProfileAge.get(userId);
    }

    public String getUserProfileGender(long userId) {
        return userProfileGender.get(userId);
    }

    public String getUserProfilePartnerGender(long userId) {
        return userProfilePartnerGender.get(userId);
    }

    public String getUserProfileInfo(long userId) {
        return userProfileInfo.get(userId);
    }

    public String getUserProfilePhoto(long userId) {
        return userProfilePhoto.get(userId);
    }

    public List<CityProfile> getUserProfileLocationList(long userId) {
        return userProfileLocationList.get(userId);
    }

    public void deleteUserProfile(long userId) {
        userProfileName.remove(userId);
        userProfileAge.remove(userId);
        userProfileGender.remove(userId);
        userProfilePartnerGender.remove(userId);
        userProfileInfo.remove(userId);
        userProfilePhoto.remove(userId);
    }

}
