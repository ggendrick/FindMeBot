package com.ggenrick.fndmebot.service;


import com.ggenrick.fndmebot.data.CityProfile;
import com.ggenrick.fndmebot.repository.CityProfileMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityProfileDataService {
    CityProfileMongoRepository cityProfileMongoRepository;
    @Autowired
    CityProfileDataService(CityProfileMongoRepository cityMongoRepository){
        this.cityProfileMongoRepository=cityMongoRepository;
    }
    public List<CityProfile> findCity(String city){
        List<CityProfile> citylist = cityProfileMongoRepository.findByCity(city);
        return citylist;
    }
    public CityProfile findCityById(int cityId){
        return cityProfileMongoRepository.findById(cityId);
    }
    public void SaveCity(CityProfile cityProfile){
        cityProfileMongoRepository.save(cityProfile);
    }
}
