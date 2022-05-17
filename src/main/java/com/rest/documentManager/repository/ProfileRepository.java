package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query(value = "SELECT u.profiles FROM User u WHERE u.email LIKE :email")
    List<Profile> findProfiles(String email);
}
