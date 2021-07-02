package com.ggenrick.fndmebot.service;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.handlers.ShowProfileHandler;
import com.ggenrick.fndmebot.handlers.WorkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class SendPhotoService {
    WorkHandler workHandler;
    ShowProfileHandler showProfileHandler;
    UserProfileController userProfileController;

    @Autowired
    SendPhotoService(WorkHandler workHandler, ShowProfileHandler showProfileHandler, UserProfileController userProfileController) {
        this.workHandler = workHandler;
        this.showProfileHandler = showProfileHandler;
        this.userProfileController = userProfileController;
    }

    public SendPhoto photoReply(Update update) {
        Long chatId = update.getMessage().getChatId();
        BotState botState = userProfileController.getUserBotState(chatId);
        SendPhoto sendPhoto = new SendPhoto();
        if (botState == BotState.SEND_PROFILE) {
            sendPhoto = showProfileHandler.showProfile(update);
        } else if (botState == BotState.WORK) {

            sendPhoto = workHandler.sendCurrentProfile(update);
        }
        sendPhoto.setChatId(chatId.toString());
        return sendPhoto;

    }
}
