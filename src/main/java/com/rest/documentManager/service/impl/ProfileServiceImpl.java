package com.rest.documentManager.service.impl;

import com.rest.documentManager.entity.Profile;
import com.rest.documentManager.repository.ProfileRepository;
import com.rest.documentManager.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public List<Profile> findById(Long id) {
        return profileRepository.findAllById(Arrays.asList(id));
    }

    @Override
    public List<Profile> fetchAllProfiles() {
        return profileRepository.findAll();
    }
}
