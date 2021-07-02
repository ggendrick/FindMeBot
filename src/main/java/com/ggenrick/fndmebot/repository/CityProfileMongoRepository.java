package com.ggenrick.fndmebot.repository;

import com.ggenrick.fndmebot.data.CityProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CityProfileMongoRepository extends MongoRepository <CityProfile, Integer> {
    List<CityProfile> findByCity(String city);
    CityProfile findById(int id);
}
