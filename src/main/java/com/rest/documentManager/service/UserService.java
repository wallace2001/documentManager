package com.rest.documentManager.service;

import com.rest.documentManager.entity.User;
import com.rest.documentManager.response.PasswordChangeData;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService extends UserDetailsService {
    User saveUser(User user) throws MessagingException;

    Boolean findByUsername(String username);

    Boolean findByEmail(String email);

    User findByLoginAndPassword(String email, String password);

    UserDetails loadUserByUsername(String username);

    List<Object> findAllUsers();

    void activeAccount(String code);

    User changePassword(PasswordChangeData data) throws MessagingException;

    User sendEmailChangedPassword(String email) throws MessagingException;
}
