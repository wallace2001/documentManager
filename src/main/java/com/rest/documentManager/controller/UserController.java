package com.rest.documentManager.controller;

import com.rest.documentManager.config.jwt.JwtProvider;
import com.rest.documentManager.dto.UserDto;
import com.rest.documentManager.entity.Category;
import com.rest.documentManager.entity.Profile;
import com.rest.documentManager.entity.Size;
import com.rest.documentManager.entity.User;
import com.rest.documentManager.repository.CategoryRepository;
import com.rest.documentManager.repository.SizeRepository;
import com.rest.documentManager.request.AuthRequest;
import com.rest.documentManager.response.*;
import com.rest.documentManager.service.ProfileService;
import com.rest.documentManager.service.UserService;
import com.rest.documentManager.service.impl.SpriteServiceImpl;
import com.rest.documentManager.services.exceptions.ErrorLoginException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    private static final String URL_API = "/api/";
    private static final String URL_PUBLIC = "/public/";

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SpriteServiceImpl spriteServiceImpl;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    // SAVE A NEW PROFILE
    @PostMapping(URL_PUBLIC + "profile")
    public ResponseEntity<Object> saveProfile(@RequestBody Profile profile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.saveProfile(profile));
    }

    // LIST A PROFILES
    @GetMapping(URL_PUBLIC + "profile")
    public List<Profile> fetchAllProfile() {
        return profileService.fetchAllProfiles();
    }

    // REGISTER CONTROLLER
    @PostMapping(URL_PUBLIC + "register")
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) throws MessagingException {
        if (userService.findByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        if (userService.findByUsername(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setProfiles(userDto.getProfiles());
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        User userSaved = userService.saveUser(user);
        UserData userData = new UserData(userSaved.getId(), userSaved.getName(), userSaved.getEmail(), "1213", userSaved.getUsername(), userSaved.getProfiles());

        return ResponseEntity.status(HttpStatus.CREATED).body(userData);
    }


    // AUTHENTICATION LOGIN
    @PostMapping(URL_PUBLIC + "auth")
    public AuthResponse auth(@RequestBody AuthRequest authRequest) {
        User user = userService.findByLoginAndPassword(authRequest.getLogin(), authRequest.getPassword());

        if (!user.getActive()) {
            throw new ErrorLoginException("Account dont verified!");
        }
        String token = JwtProvider.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getUsername(), token);
    }

    // CONFIRM ACCOUNT
    @GetMapping(URL_PUBLIC + "confirmation/{code}")
    public ResponseEntity<ConfirmationData> confirmationAccount(@PathVariable(value = "code") String code, RedirectAttributes attr) {
        System.out.println("Code" + code);
        userService.activeAccount(code);

       return ResponseEntity.status(HttpStatus.OK).body(new ConfirmationData(200, "Your account have verified!", "active Account", LocalDateTime.now()));
    }

    // SEND CONFIRMATION CHANGE PASSWORD TO EMAIL
    @PostMapping(URL_PUBLIC + "send/request/password")
    public ResponseEntity<ConfirmationData> forgotThePassword(@RequestBody EmailData data) throws MessagingException {
        User user = userService.sendEmailChangedPassword(data.getEmail());

        if (user.getCodeVerify() == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ConfirmationData(400, "Error to send a email!", "Email dont sent", LocalDateTime.now()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ConfirmationData(200, "Confirmation email sent, check your inbox or spam to change your password!", "Email sent", LocalDateTime.now()));
    }

    // CHANGE PASSWORD
    @PostMapping(URL_PUBLIC + "change/password")
    public ResponseEntity<ConfirmationData> changePassword(@RequestBody PasswordChangeData data) throws MessagingException {

        if (!data.getPassword().equals(data.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ConfirmationData(
                            400,
                            "Passwords must be the same!",
                            "Password dont changed!",
                            LocalDateTime.now())
                    );
        }

        User user = userService.changePassword(data);

        return ResponseEntity.status(HttpStatus.OK).body(new ConfirmationData(200, "Password changed success!", "Password Change", LocalDateTime.now()));
    }


    // FETCH ALL PRODUCTS
    @GetMapping("/fetch/products")
    public ResponseEntity<Page<SpriteResponse>> fetchAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("category_id") Long category_id,
            @RequestParam("size_id") Long size_id) {

        return ResponseEntity.status(HttpStatus.OK).body(spriteServiceImpl.fetchAllProducts(pageable, category_id, size_id));
    }

    // AFTER HERE, ROUTES PROTECTED
    @GetMapping(URL_API)
    public ResponseEntity<List<Object>> fetchAllUsers() {

        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsers());
    }

    // FETCH CATEGORIES
    @GetMapping(URL_PUBLIC + "fetch/categories")
    public List<Category> fetchCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping(URL_PUBLIC + "fetch/sizes")
    public List<Size> fetchSizes() {
        return sizeRepository.findAll();
    }

    @PostMapping(URL_PUBLIC + "send/email")
    public void sendEmail(@RequestBody SendEmailResponse sendEmailResponse) throws MessagingException {
        userService.sendEmailToAdmin(sendEmailResponse);
    }

    @GetMapping(URL_PUBLIC + "fetch/total/products")
    public ResponseEntity<Object> fetchTotalPageProductByUser() {
        return ResponseEntity.status(HttpStatus.OK).body(spriteServiceImpl.fetchTotalPageProductByUser());
    }
}
