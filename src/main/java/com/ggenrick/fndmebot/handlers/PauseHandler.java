package com.ggenrick.fndmebot.handlers;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.ChatIdList;
import com.ggenrick.fndmebot.service.UserProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PauseHandler {
    UserProfileController userProfileController;
    UserProfileDataService userProfileDataService;
    ChatIdList chatIdList;
    @Value("${view.profiles}")
    private String viewProfiles;
    @Value("${info.profiledeleted}")
    private String profileDeleted;
    @Autowired
    PauseHandler(UserProfileController userProfileController, UserProfileDataService userProfileDataService, ChatIdList chatIdList) {
        this.userProfileController = userProfileController;
        this.userProfileDataService = userProfileDataService;
        this.chatIdList = chatIdList;
    }

    public SendMessage pauseReply(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.getMessage().getText().equals(viewProfiles)) {
            userProfileController.setUsersBotState(update.getMessage().getChatId(), BotState.WORK);
            sendMessage.setText(viewProfiles);
            return sendMessage;
        } else {
            userProfileDataService.deleteUsersProfileData(update.getMessage().getChatId().toString());
            sendMessage.setText(profileDeleted);
            chatIdList.update();
            return sendMessage;
        }
    }
}
