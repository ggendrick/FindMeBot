package com.ggenrick.fndmebot;


import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.service.InputMessageVerificationService;
import com.ggenrick.fndmebot.service.SendMessageService;
import com.ggenrick.fndmebot.service.SendPhotoService;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
@Setter
@ConfigurationProperties(prefix = "bot")
@Slf4j
public class Bot extends TelegramWebhookBot {
    private String botName;
    private String botToken;
    private String botWebHookPath;
    private SendMessageService sendMessageService;
    private SendPhotoService sendPhotoService;
    private UserProfileController userProfileController;
    private InputMessageVerificationService inputMessageVerificationService;
    public static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private BotState botState;
    @Value("${view.profiles}")
    private String viewProfiles;
    @Autowired
    public Bot(SendMessageService sendMessageService, SendPhotoService sendPhotoService, UserProfileController userProfileController, InputMessageVerificationService inputMessageVerificationService) {
        super();
        this.sendMessageService = sendMessageService;
        this.sendPhotoService = sendPhotoService;
        this.userProfileController = userProfileController;
        this.inputMessageVerificationService = inputMessageVerificationService;

    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        botState = userProfileController.getUserBotState(update.getMessage().getChatId());
        log(update);

        SendMessage reply = new SendMessage();
        reply = inputMessageVerificationService.verivficateMessage(update);

        if (reply.getText().equals("ошибка ввода")) {
            return reply;
        }
        if (botState != BotState.SEND_PROFILE && botState != BotState.WORK) {
            reply = sendMessageService.messagereply(update);
            botState = userProfileController.getUserBotState(update.getMessage().getChatId());
            if (!reply.getText().equals(viewProfiles)) {
                return reply;
            }
        }

        if ((botState == BotState.SEND_PROFILE) || (botState == BotState.WORK)) {
            SendPhoto sendPhoto = sendPhotoService.photoReply(update);
            log(update);
            if (sendPhoto.getPhoto() != null) {
                execute(sendPhoto);
            } else { //если вернулся null - pause

                return onPauseSetKeyboard(update);
            }
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return botWebHookPath;
    }

    public SendMessage onPauseSetKeyboard(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("вы на паузе");
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(viewProfiles);
        keyboardFirstRow.add("удалить анкету");
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public void log(Update update) {
        LOGGER.info(
                String.format("text: %s, state: %s, chatid: %s", update.getMessage().getText(), userProfileController.getUserBotState(update.getMessage().getChatId()),
                        update.getMessage().getChatId())
        );
    }


}
