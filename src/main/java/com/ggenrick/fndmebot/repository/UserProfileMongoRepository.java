package com.ggenrick.fndmebot.repository;

import com.ggenrick.fndmebot.data.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileMongoRepository extends MongoRepository<UserProfile,String> {
    UserProfile findByChatId(long chatId);
    void deleteAllByChatId(long chatId);
    List<UserProfile> findByLocationId(int locationId);
}
