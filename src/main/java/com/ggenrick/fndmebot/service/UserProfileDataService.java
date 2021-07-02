package com.ggenrick.fndmebot.service;

import com.ggenrick.fndmebot.data.UserProfile;
import com.ggenrick.fndmebot.repository.UserProfileMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileDataService {
    private UserProfileMongoRepository userProfileMongoRepository;

    public UserProfileDataService(UserProfileMongoRepository userProfileMongoRepository) {
        this.userProfileMongoRepository = userProfileMongoRepository;
    }

    public List<UserProfile> getAllProfiles() {
        return userProfileMongoRepository.findAll();
    }

    public void saveUserProfileData(UserProfile userProfile) {
        userProfileMongoRepository.save(userProfile);

    }

    public void deleteUsersProfileData(String profileDataId) {
        userProfileMongoRepository.deleteById(profileDataId);
    }

    public UserProfile getUserProfileData(long chatId) {
        return userProfileMongoRepository.findByChatId(chatId);
    }
    public List<UserProfile> getUsersProfileByCity(int cityId){
        return userProfileMongoRepository.findByLocationId(cityId);
    }
    public boolean findUser(long chatId) {
        if ((userProfileMongoRepository.findByChatId(chatId)) != null) {
            return true;
        } else return false;
    }

}
