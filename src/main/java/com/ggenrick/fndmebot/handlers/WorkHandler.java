package com.ggenrick.fndmebot.handlers;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.ChatIdList;
import com.ggenrick.fndmebot.data.UserProfile;
import com.ggenrick.fndmebot.service.CityProfileDataService;
import com.ggenrick.fndmebot.service.UserProfileDataService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class WorkHandler {
    ChatIdList chatIdList;
    UserProfileController userProfileController;
    UserProfileDataService userProfileDataService;
    CityProfileDataService cityProfileDataService;
    private Map<Long, Long> currentProfileId = new HashMap<>();
    private int randomProfileNumber;
    private int profileCount;
    @Value("${view.profile}")
    private String viewProfile;
    @Value("${view.profiles}")
    private String viewProfiles;
    @Value("${male.first}")
    private String maleFirst;
    @Value("${male.third}")
    private String maleThird;
    @Value("${female.first}")
    private String femaleFirst;
    @Value("${female.third}")
    private String femaleThird;
    @Value("${reply.like}")
    private String like;
    @Value("${reply.dislike}")
    private String dislike;
    @Value("${reply.pause}")
    private String pause;
    @Autowired
    WorkHandler(ChatIdList chatIdList, UserProfileController userProfileController, UserProfileDataService userProfileDataService, CityProfileDataService cityProfileDataService) {
        this.chatIdList = chatIdList;
        this.userProfileController = userProfileController;
        this.userProfileDataService = userProfileDataService;
        this.cityProfileDataService = cityProfileDataService;
    }

    @SneakyThrows
    public SendPhoto sendCurrentProfile(Update update) {
        SendPhoto sendPhoto = new SendPhoto();
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        long currentId = -1;
        UserProfile userProfile;
        userProfile = userProfileDataService.getUserProfileData(chatId);
        UserProfile userCurrentProfile = new UserProfile();
        if (message.equals(viewProfiles)) {
            userCurrentProfile = findCurrentProfile(userProfile);
            if(userCurrentProfile==null){
                return noProfiles(userProfile);
            }
            currentProfileId.put(chatId, userCurrentProfile.getChatId());
            InputFile inputFile = new InputFile(userCurrentProfile.getPhoto());
            sendPhoto.setPhoto(inputFile);

            sendPhoto.setCaption(
                    String.format("%s,\t%d,\n%s,\n%s",userCurrentProfile.getName(),userCurrentProfile.getAge(),
                            cityProfileDataService.findCityById(userCurrentProfile.getLocationId()).getCity(),userCurrentProfile.getInfo()));
            sendPhoto.setChatId(Long.toString(chatId));
            sendPhoto = setKeyBoard(sendPhoto);
        } else if (message.equals(like)) {
            currentId = currentProfileId.get(chatId);
            userCurrentProfile = userProfileDataService.getUserProfileData(currentId);
            if(userCurrentProfile==null){
                return noProfiles(userProfile);
            }
            if (userCurrentProfile.getLikedId().contains(chatId)) {
                List<Long> likedIdList = userCurrentProfile.getLikedId();
                likedIdList.remove(chatId);
                userCurrentProfile.setLikedId(likedIdList);
                InputFile inputFile = new InputFile(userCurrentProfile.getPhoto());
                sendPhoto.setPhoto(inputFile);
                sendPhoto.setCaption(String.format("Есть взаимная симпатия: \n %s,\t %d \n %s\n t.me/%s",
                        userCurrentProfile.getName(), userCurrentProfile.getAge(), userCurrentProfile.getInfo(), userCurrentProfile.getUsername()));

                KeyboardRow keyboardFirstRow = new KeyboardRow();
                keyboardFirstRow.add(viewProfiles);
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                keyboard.add(keyboardFirstRow);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setKeyboard(keyboard);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                sendPhoto.setReplyMarkup(replyKeyboardMarkup);
                userProfileDataService.saveUserProfileData(userProfile);
                userProfileDataService.saveUserProfileData(userCurrentProfile);
            } else {
                List<Long> likedIdList = userProfile.getLikedId();
                if (!likedIdList.contains(currentId)) likedIdList.add(currentId);
                userProfile.setLikedId(likedIdList);
                userCurrentProfile = findCurrentProfile(userProfile);
                if(userCurrentProfile==null){
                    return noProfiles(userProfile);
                }
                currentProfileId.put(chatId, userCurrentProfile.getChatId());
                InputFile inputFile = new InputFile(userCurrentProfile.getPhoto());
                sendPhoto.setPhoto(inputFile);
                sendPhoto.setCaption(
                        String.format("%s,\t%d,\n%s,\n%s",userCurrentProfile.getName(),userCurrentProfile.getAge(),
                                cityProfileDataService.findCityById(userCurrentProfile.getLocationId()).getCity(),userCurrentProfile.getInfo()));
                sendPhoto.setChatId(Long.toString(chatId));
                sendPhoto = setKeyBoard(sendPhoto);
                userProfileDataService.saveUserProfileData(userProfile);
            }
        } else if (message.equals(dislike)) {
            userCurrentProfile = findCurrentProfile(userProfile);
            if(userCurrentProfile==null){
                return noProfiles(userProfile);
            }
            currentProfileId.put(chatId, userCurrentProfile.getChatId());
            InputFile inputFile = new InputFile(userCurrentProfile.getPhoto());
            sendPhoto.setPhoto(inputFile);
                    sendPhoto.setCaption(
                            String.format("%s,\t%d,\n%s,\n%s",userCurrentProfile.getName(),userCurrentProfile.getAge(),
                                    cityProfileDataService.findCityById(userCurrentProfile.getLocationId()).getCity(),userCurrentProfile.getInfo()));
            sendPhoto.setChatId(Long.toString(chatId));
            sendPhoto = setKeyBoard(sendPhoto);
        } else if (message.equals(pause)) {
            userProfileController.setUsersBotState(userProfile.getChatId(), BotState.PAUSE);


        }
        sendPhoto.setChatId(Long.toString(chatId));
        return sendPhoto;


    }

    private UserProfile findCurrentProfile(UserProfile userProfile) {
        long prevUserProfileChatID;
        if(currentProfileId.get(userProfile.getChatId())!=null) {
            prevUserProfileChatID = currentProfileId.get(userProfile.getChatId());
        }
        else prevUserProfileChatID=-1;
        profileCount = chatIdList.getIdCount(userProfile.getLocationId());
        int profileSuitableCount = chatIdList.getsuitableIdCout(userProfile.getLocationId(),userProfile);
        if(profileSuitableCount==0){return null;}
        if(profileSuitableCount<=1&&prevUserProfileChatID!=-1){
            return userProfileDataService.getUserProfileData(prevUserProfileChatID);
        }
        randomProfileNumber = ThreadLocalRandom.current().nextInt(0, profileCount);
        UserProfile userCurrentProfile;
        userCurrentProfile = userProfileDataService.getUserProfileData(chatIdList.getUser(userProfile.getLocationId(),randomProfileNumber));

        long userProfileChatId = userProfile.getChatId();
        long userCurrentProfileChatId = userCurrentProfile.getChatId();
        while ((userProfile.getPartnerGender().equals(femaleThird) && userCurrentProfile.getGender().equals(maleFirst)) ||
                (userProfile.getPartnerGender().equals(maleThird) && userCurrentProfile.getGender().equals(femaleFirst)) ||
                (userProfileChatId == userCurrentProfileChatId)||userCurrentProfileChatId==prevUserProfileChatID) {
            randomProfileNumber = ThreadLocalRandom.current().nextInt(0, profileCount);
            userCurrentProfile = userProfileDataService.getUserProfileData(chatIdList.getUser(userProfile.getLocationId(),randomProfileNumber));
            userCurrentProfileChatId = userCurrentProfile.getChatId();


        }



        return userCurrentProfile;
    }

    SendPhoto setKeyBoard(SendPhoto sendPhoto) {
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(like);
        keyboardFirstRow.add(dislike);
        keyboardFirstRow.add(pause);
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        return sendPhoto;
    }

    SendPhoto noProfiles(UserProfile userProfile){
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(userProfile.getPhoto());
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption("Извини, в твоем городе пока нет анкет, попробуй написать позже или сменить город");
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(viewProfiles);
        keyboardFirstRow.add("/start");
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        sendPhoto.setChatId(userProfile.getChatId().toString());
        return sendPhoto;
    }
}
