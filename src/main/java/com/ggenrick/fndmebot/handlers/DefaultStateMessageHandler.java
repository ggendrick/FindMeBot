package com.ggenrick.fndmebot.handlers;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
@Setter
@ConfigurationProperties(prefix = "defaultstatemessagehandler")
public class DefaultStateMessageHandler {
    UserProfileController userProfileController;
    BotState botState;
    String askCreate;
    @Value("${comand.create}")
    private String create;
    @Autowired
    DefaultStateMessageHandler(UserProfileController userProfileController) {
        this.userProfileController = userProfileController;
    }

    public SendMessage defaultMessageReply() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(askCreate);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(create);
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }


}
