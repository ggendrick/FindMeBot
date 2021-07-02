package com.ggenrick.fndmebot.service;


import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Service
public class SendMessageService {
    UserProfileController userProfileController;
    BotState botState;
    ProfileMessageHandler profileMessageHandler;
    public ShowProfileHandler showProfileHandler;
    WorkHandler workHandler;
    PauseHandler pauseHandler;
    DefaultStateMessageHandler defaultStateMessageHandler;
    UserProfileDataService userProfileDataService;
    @Value("${reply.alreadyhave}")
    private String alreadyHave;
    @Value("${view.profile}")
    private String viewProfile;
    @Autowired
    SendMessageService(UserProfileController userProfileController, ProfileMessageHandler profileMessageHandler, ShowProfileHandler showProfileHandler,
                       WorkHandler workHandler, PauseHandler pauseHandler, DefaultStateMessageHandler defaultStateMessageHandler, UserProfileDataService userProfileDataService) {
        this.userProfileController = userProfileController;
        this.profileMessageHandler = profileMessageHandler;
        this.showProfileHandler = showProfileHandler;
        this.workHandler = workHandler;
        this.pauseHandler = pauseHandler;
        this.defaultStateMessageHandler = defaultStateMessageHandler;
        this.userProfileDataService = userProfileDataService;
    }

    public SendMessage messagereply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String inputString = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        if (inputString != null) {
            switch (inputString) {
                case "/start":
                    botState = BotState.DEFAULT;
                    break;
                case "/create":
                    botState = BotState.CREATE;
                    break;
                case "/pause":
                    botState = BotState.PAUSE;
                    break;
                default:
                    botState = userProfileController.getUserBotState(userId);
            }
            userProfileController.setUsersBotState(userId, botState);
        }
        botState = userProfileController.getUserBotState(userId);
        if (userProfileDataService.findUser(userId) && inputString != null && inputString.equals("/start")) { //если анкета уже создана, но состояние сброшено
            userProfileController.setUsersBotState(userId, BotState.SEND_PROFILE);
            sendMessage.setText(alreadyHave);
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(viewProfile);
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(keyboardFirstRow);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboard);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        } else if (botState == BotState.DEFAULT) {             //выбор обработчика
            sendMessage = defaultStateMessageHandler.defaultMessageReply();
        }

        if (fillprofilestatus(botState)) {
            sendMessage = profileMessageHandler.profileMessageRepy(update);
        }
        if (botState == BotState.ASK_PROFILE_APROOVE) {
            sendMessage = profileMessageHandler.profileMessageRepy(update);
        }
        if (botState == BotState.PAUSE) {
            sendMessage = pauseHandler.pauseReply(update);
        }
        sendMessage.setChatId(chatId);
        return sendMessage;

    }

    boolean fillprofilestatus(BotState botState) {
        switch (botState) {
            case CREATE:
                return true;
            case ASK_NAME:
                return true;
            case ASK_AGE:
                return true;
            case ASK_GENDER:
                return true;
            case ASK_PARTNER_GENDER:
                return true;
            case ASK_INFO:
                return true;
            case ASK_LOCATION:
                return true;
            case ASK_APROOVE_LOCATION:
                return true;
            case ASK_PHOTO:
                return true;
            default:
                return false;
        }

    }
}
