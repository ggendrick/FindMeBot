package com.ggenrick.fndmebot.handlers;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.UserProfile;
import com.ggenrick.fndmebot.service.CityProfileDataService;
import com.ggenrick.fndmebot.service.UserProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class ShowProfileHandler {
    UserProfileController userProfileController;
    UserProfile userProfile;
    UserProfileDataService userProfileDataService;
    CityProfileDataService cityProfileDataService;
    @Value("${reply.yes}")
    private String yes;
    @Value("${reply.no}")
    private String no;

    @Autowired
    ShowProfileHandler(UserProfileController userProfileController, UserProfileDataService userProfileDataService, CityProfileDataService cityProfileDataService) {
        this.userProfileController = userProfileController;
        this.userProfileDataService = userProfileDataService;
        this.cityProfileDataService = cityProfileDataService;
    }

    public SendPhoto showProfile(Update update) {
        SendPhoto sendPhoto = new SendPhoto();
        UserProfile userProfile = userProfileDataService.getUserProfileData(update.getMessage().getChatId());
        InputFile inputFile = new InputFile(userProfile.getPhoto());
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(update.getMessage().getChatId().toString());

        sendPhoto.setCaption(String.format("имя: %s \nвозраст: %d\n%s\nты: %s\nхочешь найти: %s\nинформация о себе: %s\nвсе верно?",
                userProfile.getName(), userProfile.getAge(),cityProfileDataService.findCityById(userProfile.getLocationId()).getCity(), userProfile.getGender(), userProfile.getPartnerGender(), userProfile.getInfo()));
        userProfileController.setUsersBotState(update.getMessage().getChatId(), BotState.ASK_PROFILE_APROOVE);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(yes);
        keyboardFirstRow.add(no);
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        return sendPhoto;

    }
}
