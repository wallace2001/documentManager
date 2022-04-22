package com.rest.documentManager.service;

import com.rest.documentManager.entity.Profile;

import java.util.List;

public interface ProfileService {

    Profile saveProfile(Profile profile);

    List<Profile> findById(Long id);

    List<Profile> fetchAllProfiles();
}
