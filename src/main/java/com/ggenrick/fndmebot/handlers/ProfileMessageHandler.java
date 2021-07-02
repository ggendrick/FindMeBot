package com.ggenrick.fndmebot.handlers;

import com.ggenrick.fndmebot.controller.UserProfileController;
import com.ggenrick.fndmebot.data.BotState;
import com.ggenrick.fndmebot.data.ChatIdList;
import com.ggenrick.fndmebot.data.CityProfile;
import com.ggenrick.fndmebot.data.UserProfile;
import com.ggenrick.fndmebot.service.CityProfileDataService;
import com.ggenrick.fndmebot.service.UserProfileDataService;
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
import java.util.List;

@Component
@Setter
@ConfigurationProperties(prefix = "profliemessagehandler")
public class ProfileMessageHandler {
    private String create;
    private String askName;
    private String askAge;
    private String askGender;
    private String askPartnerGender;
    private String askInfo;
    private String askPhoto;
    private BotState botState;
    UserProfileController userProfileController;
    UserProfileDataService userProfileDataService;
    CityProfileDataService cityProfileDataService;
    ChatIdList chatIdList;
    private UserProfile userProfile;
    private List<CityProfile> cityProfileList;
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
    @Value("${info.wellletsstart}")
    private String letsStart;
    @Value("${info.fillprofileagain}")
    private String fillprofileagain;


    @Autowired
    public ProfileMessageHandler(UserProfileController userProfileController, UserProfileDataService userProfileDataService, CityProfileDataService cityProfileDataService,
                                 ChatIdList chatIdList) {
        this.userProfileController = userProfileController;
        this.userProfileDataService = userProfileDataService;
        this.cityProfileDataService = cityProfileDataService;
        this.userProfile = new UserProfile();
        this.chatIdList = chatIdList;
        cityProfileList=new ArrayList<CityProfile>();
    }

    public SendMessage profileMessageRepy(Update update) {
        botState = userProfileController.getUserBotState(update.getMessage().getFrom().getId());
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        String message = update.getMessage().getText();
        if (botState == BotState.CREATE) {
            sendMessage.setText(create);
            userProfileController.setUsersBotState(chatId, BotState.ASK_NAME);
        }
        if (botState == BotState.ASK_NAME) {
            sendMessage.setText(askName);
            userProfileController.setUserProfileName(chatId, message);
            userProfileController.setUsersBotState(chatId, BotState.ASK_AGE);
        }
        if (botState == BotState.ASK_AGE) {
            sendMessage.setText(askAge);
            userProfileController.setUserProfileAge(chatId, Integer.parseInt(message));
            userProfileController.setUsersBotState(chatId, BotState.ASK_GENDER);
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(maleFirst);
            keyboardFirstRow.add(femaleFirst);
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(keyboardFirstRow);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboard);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        if (botState == BotState.ASK_GENDER) {
            sendMessage.setText(askGender);
            userProfileController.setUserProfileGender(chatId, message);
            userProfileController.setUsersBotState(chatId, BotState.ASK_PARTNER_GENDER);
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(maleThird);
            keyboardFirstRow.add(femaleThird);
            keyboardFirstRow.add(noDifference);
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(keyboardFirstRow);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboard);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        if (botState == BotState.ASK_PARTNER_GENDER) {
            sendMessage.setText(askPartnerGender);
            userProfileController.setUserProfilePartnerGender(chatId, message);
            userProfileController.setUsersBotState(chatId, BotState.ASK_INFO);
        }
        if (botState == BotState.ASK_INFO) {
            sendMessage.setText("введи свой город\nназвание города следует вводить с большой буквы");
            userProfileController.setUserProfileInfo(chatId, message);
            userProfileController.setUsersBotState(chatId, BotState.ASK_LOCATION);
        }
        if(botState==BotState.ASK_LOCATION){
            cityProfileList=cityProfileDataService.findCity(message);

            if(cityProfileList.size()==0){
                sendMessage.setText("город не найден, попробуйте ввести другой");
            }
            else {
                int count = 1;
                String text="По вашему запросу найдены следующие города, выберите соответствующую цифру:\n";

                for(CityProfile cityProfile:cityProfileList){
                    text += count+". "+cityProfile.getCity()+"\n"+cityProfile.getRegion()+"\n";
                    count++;
                }
                text+="0. Моего города нет в списке\n";
                sendMessage.setText(text);
                userProfileController.setUserProfileLocationList(chatId,cityProfileList);
                userProfileController.setUsersBotState(chatId,BotState.ASK_APROOVE_LOCATION);

                KeyboardRow keyboardFirstRow = new KeyboardRow();
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();

                for(int i=1;i<count;i++){
                    keyboardFirstRow.add(String.format("%d",i));
                }
                keyboardFirstRow.add("0");
                keyboard.add(keyboardFirstRow);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setKeyboard(keyboard);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            }
        }

        if(botState==BotState.ASK_APROOVE_LOCATION){
            cityProfileList=userProfileController.getUserProfileLocationList(chatId);
            int intmessage = Integer.parseInt(message);
            if(message.equals("0")){
                sendMessage.setText("Введите корректный город");
                userProfileController.setUsersBotState(chatId,BotState.ASK_LOCATION);
            }
            else {
                userProfileController.setUserProfileLocationId(chatId,cityProfileList.get(intmessage-1).getId());
                userProfileController.setUsersBotState(chatId,BotState.ASK_PHOTO);
                sendMessage.setText(askInfo);

            }
        }

        if (botState == BotState.ASK_PHOTO) {
            sendMessage.setText(askPhoto);
            userProfileController.setUserProfilePhoto(chatId, update.getMessage().getPhoto().get(0).getFileId());
            userProfileController.setUsersBotState(chatId, BotState.SEND_PROFILE);
            userProfile.setName(userProfileController.getUserProfileName(chatId));
            userProfile.setAge(userProfileController.getUserProfileAge(chatId));
            userProfile.setGender(userProfileController.getUserProfileGender(chatId));
            userProfile.setPartnerGender(userProfileController.getUserProfilePartnerGender(chatId));
            userProfile.setInfo(userProfileController.getUserProfileInfo(chatId));
            userProfile.setPhoto(userProfileController.getUserProfilePhoto(chatId));
            userProfile.setLocationId(userProfileController.getUserProfileLocationId(chatId));
            userProfile.setUsername(update.getMessage().getFrom().getUserName());
            userProfile.setId(chatId.toString());
            userProfile.setChatId(chatId);
            userProfile.setLikedId(new ArrayList<>());
            userProfileDataService.saveUserProfileData(userProfile);
            userProfileController.deleteUserProfile(chatId);
            chatIdList.update();
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(viewProfile);
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(keyboardFirstRow);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboard);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        if (botState == BotState.ASK_PROFILE_APROOVE) {
            if (message.equals(yes)) {
                userProfileController.setUsersBotState(chatId, BotState.WORK);
                sendMessage.setText(letsStart);
                KeyboardRow keyboardFirstRow = new KeyboardRow();
                keyboardFirstRow.add(viewProfiles);
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                keyboard.add(keyboardFirstRow);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setKeyboard(keyboard);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            } else if (message.equals(no)) {
                userProfileController.setUsersBotState(chatId, BotState.CREATE);
                sendMessage.setText(fillprofileagain);
                KeyboardRow keyboardFirstRow = new KeyboardRow();
                keyboardFirstRow.add(ok);
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                keyboard.add(keyboardFirstRow);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setKeyboard(keyboard);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);

            }


        }
        return sendMessage;
    }
}
