package com.ggenrick.fndmebot.data;

import com.ggenrick.fndmebot.service.UserProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatIdList {
    private Map<Integer, List<Long>> idListMap = new HashMap<>();
    UserProfileDataService userProfileDataService;
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

    @Autowired
    ChatIdList(UserProfileDataService userProfileDataService) {
        this.userProfileDataService = userProfileDataService;
        update();
    }

    public void update() {
        List<UserProfile> userProfileList = userProfileDataService.getAllProfiles();
        for (UserProfile userProfile : userProfileList) {
            List<Long> temp;
            temp = idListMap.getOrDefault(userProfile.getLocationId(), new ArrayList<Long>());
            temp.add(userProfile.getChatId());
            idListMap.put(userProfile.getLocationId(), temp);
        }
    }

    public int getIdCount(int cityId) {
        return idListMap.get(cityId).size();
    }

    public int getsuitableIdCout(int cityId, UserProfile userProfile) {
        int count = 0;
        for (Long id : idListMap.get(cityId)) {
            if ((userProfile.getPartnerGender().equals(maleThird) && userProfileDataService.getUserProfileData(id).getGender().equals(femaleThird)
                    || userProfile.getPartnerGender().equals(femaleThird) && userProfileDataService.getUserProfileData(id).getGender().equals(femaleFirst) ||
                    userProfile.getPartnerGender().equals(noDifference)) && userProfile.getChatId() != id) {
                count++;
            }

        }
        return count;
    }

    public long getUser(int cityId, int listId) {
        return idListMap.get(cityId).get(listId);
    }


}
