package com.ggenrick.fndmebot.service;

import com.ggenrick.fndmebot.Bot;
import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.CityProfile;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class InputMessageVerificationService {
    UserProfileController userProfileController;
    BotState botState;
    @Value("${male.first}")
    private String maleFirst;
    @Value("${male.third}")
    private String maleThird;
    @Value("${female.first}")
    private String femaleFirst;
    @Value("${female.third}")
    private String femaleThird;
    @Value("${no.Difference}")
    private String noDifference;

    @Value("${view.profile}")
    private String viewProfile;
    @Value("${view.profiles}")
    private String viewProfiles;
    @Value("${reply.yes}")
    private String yes;
    @Value("${reply.no}")
    private String no;
    @Value("${reply.ok}")
    private String ok;
    @Value("${reply.like}")
    private String like;
    @Value("${reply.dislike}")
    private String dislike;
    @Value("${reply.pause}")
    private String pause;
    @Autowired
    InputMessageVerificationService(UserProfileController userProfileController) {
        this.userProfileController = userProfileController;
    }

    public SendMessage verivficateMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        String inputMessage = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        boolean status = false;
        botState = userProfileController.getUserBotState(chatId);
        if(botState!=BotState.ASK_PHOTO&&inputMessage==null){
            sendMessage.setText("ошибка ввода");
            return sendMessage;
        }
        if (botState == BotState.DEFAULT&&inputMessage.equals("/create")) {
            status = true;
        } else if (botState == BotState.CREATE) {
            status = true;
        } else if (botState == BotState.ASK_NAME&&inputMessage.length()<=40) {
            status = true;
        } else if (botState == BotState.ASK_AGE && NumberUtils.isParsable(inputMessage)&&Integer.parseInt(inputMessage)>18&&Integer.parseInt(inputMessage)<150) {
            status = true;
        } else if (botState == BotState.ASK_GENDER && (inputMessage.equals(maleFirst) || inputMessage.equals(femaleFirst))) {
            status = true;
        } else if (botState == BotState.ASK_PARTNER_GENDER && (inputMessage.equals(noDifference) || inputMessage.equals(maleThird) || inputMessage.equals(femaleThird))) {
            status = true;
        } else if (botState == BotState.ASK_INFO&&inputMessage.length()<=300) {
            status = true;
        } else if (botState == BotState.ASK_LOCATION) {
                status = true;
        }else if (botState == BotState.ASK_APROOVE_LOCATION&& NumberUtils.isParsable(inputMessage)) {
            List<CityProfile> cityProfileList = userProfileController.getUserProfileLocationList(chatId);
            int msg = Integer.parseInt(inputMessage);
            if(msg>=0&&msg<=cityProfileList.size())  status = true;
        }
        else if (botState == BotState.ASK_PHOTO && update.getMessage().getPhoto() != null) {
            status = true;
        } else if (botState == BotState.SEND_PROFILE && inputMessage.equals(viewProfile)) {
            status = true;
        } else if (botState == BotState.ASK_PROFILE_APROOVE && (inputMessage.equals(yes) || inputMessage.equals(no)||inputMessage.equals(viewProfile))) {
            status = true;
        } else if (botState == BotState.WORK && (inputMessage.equals(like) || inputMessage.equals(viewProfiles) || inputMessage.equals(dislike) || inputMessage.equals(pause))) {
            status = true;
        } else if (botState == BotState.PAUSE && (inputMessage.equals(viewProfiles) || inputMessage.equals("удалить анкету"))) {
            status = true;
        }
        if(inputMessage!=null&&inputMessage.equals("/start")){
            status=true;
        }
        if (status == false) {
            sendMessage.setText("ошибка ввода");
        } else sendMessage.setText(ok);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        return sendMessage;

    }


}



