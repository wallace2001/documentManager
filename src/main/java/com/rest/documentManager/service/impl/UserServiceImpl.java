package com.rest.documentManager.service.impl;

import com.rest.documentManager.entity.Profile;
import com.rest.documentManager.entity.User;
import com.rest.documentManager.exception.ResourceExceptionHandler;
import com.rest.documentManager.repository.ProfileRepository;
import com.rest.documentManager.repository.UserRepository;
import com.rest.documentManager.response.PasswordChangeData;
import com.rest.documentManager.response.SendEmailResponse;
import com.rest.documentManager.service.UserService;
import com.rest.documentManager.services.exceptions.ErrorLoginException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EmailServiceImpl emailService;

    private ResourceExceptionHandler resourceExceptionHandler;


    // SAVE USER
    @Override
    public User saveUser(User user) throws MessagingException {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        emailConfirmationRegister(user.getEmail());

        return userRepository.save(user);
    }

    // FIND USER BY USERNAME AND RETURN A BOOLEAN
    @Override
    public Boolean findByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // FIND USER BY EMAIL AND RETURN A BOOLEAN
    @Override
    public Boolean findByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // VERIFY LOGIN
    @Override
    public User findByLoginAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
            if (passwordEncoder().matches(password, user.getPassword())) {
                return user;
            } else {
                throw new ErrorLoginException("Email or Password dont valid!");
            }
        }

        throw new ErrorLoginException("Account dont exists!");
    }

    // EMAIL CONFIRMATION ACCOUNT
    public void emailConfirmationRegister(String email) throws MessagingException {
        String code = Base64Utils.encodeToString(email.getBytes());

        emailService.sendConfirmationRegisterAccount(email, code);
    }

    // ACTIVED ACCOUNT
    @Override
    public void activeAccount(String code) {
        String email = new String(Base64Utils.decodeFromString(code));
        User user = userRepository.findByEmail(email);

        if (user.getId() == null) {
            throw new ErrorLoginException("Erro ao ativar a conta!");
        } else if (user.getActive()) {
            throw new ErrorLoginException("Account has verified!");
        }

        User newUser = new User();

        BeanUtils.copyProperties(user, newUser);

        newUser.setId(user.getId());
        newUser.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        newUser.setActive(true);

        userRepository.save(newUser);
    }

    // CHANGE PASSWORD
    @Override
    public User changePassword(PasswordChangeData data) throws MessagingException {
        Optional<User> user = userRepository.findByEmailOrNot(data.getEmail());

        if (user.isPresent()) {

            if (user.get().getCodeVerify() == null || !user.get().getCodeVerify().equals(data.getCodeVerify())) {
                throw new ErrorLoginException("Invalid code!");
            }

            if (passwordEncoder().matches(data.getPassword(), user.get().getPassword())) {
                throw new ErrorLoginException("The new password cannot be the same as the old one!");
            }

            User newUser = new User();

            BeanUtils.copyProperties(user.get(), newUser);

            newUser.setId(user.get().getId());
            newUser.setPassword(new BCryptPasswordEncoder().encode(data.getPassword()));
            newUser.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            newUser.setCodeVerify(null);

            userRepository.save(newUser);

            emailService.sendNoticeToChangePassword(data.getEmail());

            return newUser;
        } else {
            throw new ErrorLoginException("Email dont found!");
        }
    }

    // SEND A EMAIL TO CHANGED PASSWORD
    @Override
    public User sendEmailChangedPassword(String email) throws MessagingException {
        Optional<User> user = userRepository.findByEmailOrNot(email);

        if (user.isPresent()) {
            String code = RandomStringUtils.randomAlphanumeric(6);
            emailService.sendEmailConfirmationChangePassword(user.get().getEmail(), code);

            User newUser = new User();

            BeanUtils.copyProperties(user.get(), newUser);

            newUser.setId(user.get().getId());
            newUser.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            newUser.setCodeVerify(code);

            userRepository.save(newUser);

            return newUser;
        }

        throw new ErrorLoginException("Email dont found!");
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Optional<User> findByEmailReturnUser(String email) {
        return userRepository.findByEmailReturnUser(email);
    }

    @Override
    public Optional<User> findByIdReturnUser(Long idUser) {
        return userRepository.findByIdReturnUser(idUser);
    }

    @Override
    public void emailConfirmationPayment(String email) throws MessagingException {
        emailService.sendNoticePaymentProduct(email);
    }

    @Override
    public void sendEmailToAdmin(SendEmailResponse sendEmailResponse) throws MessagingException {
        emailService.sendEmailToAdmin(sendEmailResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        List<Profile> profiles = profileRepository.findProfiles(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    AuthorityUtils.createAuthorityList(getAuthorities(profiles))
            );
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    public List<Object> findAllUsers() {
        return userRepository.findAllUsers();
    }

    // ENCODER A PASSWORD
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String[] getAuthorities(List<Profile> profiles) {
        String[] authorites = new String[profiles.size()];

        for (int i = 0; i < profiles.size(); i++) {
            authorites[i] = profiles.get(i).getDescricao();
        }

        return authorites;
    }
}
