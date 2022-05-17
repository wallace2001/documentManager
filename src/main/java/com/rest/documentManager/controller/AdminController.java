package com.rest.documentManager.controller;

import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import com.rest.documentManager.dto.CategoryDto;
import com.rest.documentManager.dto.SizeDto;
import com.rest.documentManager.dto.SpriteDto;
import com.rest.documentManager.entity.*;
import com.rest.documentManager.repository.AttachmentRepository;
import com.rest.documentManager.repository.CategoryRepository;
import com.rest.documentManager.repository.ImageRepository;
import com.rest.documentManager.repository.SizeRepository;
import com.rest.documentManager.response.CategoriesBoughtResponse;
import com.rest.documentManager.response.SpriteResponse;
import com.rest.documentManager.service.impl.ImageServiceImpl;
import com.rest.documentManager.service.impl.PaymentServiceImpl;
import com.rest.documentManager.service.impl.SpriteServiceImpl;
import com.rest.documentManager.service.impl.UserServiceImpl;
import com.rest.documentManager.services.exceptions.ErrorControllerException;
import com.rest.documentManager.services.exceptions.ErrorLoginException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/admin")
public class AdminController {

    public static final String SUCCESS_URL = "/payment/public/pay/success";
    public static final String CANCEL_URL = "/payment/public/pay/cancel";

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private SpriteServiceImpl spriteServiceImpl;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private PaymentServiceImpl paymentServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping
    public String admin() {
        return "ADMIN";
    }

    // SAVE CATEGORIES
    @PostMapping("/save/category")
    public ResponseEntity<Object> saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        for (int i = 0; i < categoryDto.getInstance().size(); i++) {
            String instance = categoryDto.getInstance().get(i);
            Category category = new Category();

            category.setInstance(instance);
            category.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            category.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            categoryRepository.save(category);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Categories generated!");
    }

    // SAVE SIZES
    @PostMapping("/save/size")
    public ResponseEntity<Object> saveSize(@RequestBody @Valid SizeDto sizeDto) {
        for (int i = 0; i < sizeDto.getInstance().size(); i++) {
            String instance = sizeDto.getInstance().get(i);
            Size size = new Size();

            size.setInstance(instance);
            size.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            size.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            sizeRepository.save(size);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Sizes generated!");
    }

    // SAVE PRODUCTS
    @PostMapping("/save/sprite")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveSprite(@RequestBody SpriteDto spriteDto) throws Exception {
        Optional<Sprite> spriteExists = spriteServiceImpl.findById(spriteDto.getId());
        Optional<Category> category = categoryRepository.findById(spriteDto.getCategory_id());
        Optional<Size> size = sizeRepository.findById(spriteDto.getSize_id());

        if (spriteExists.isPresent()) {
            if (category.isPresent() && size.isPresent()) {
                Sprite newSprite = new Sprite();
                BeanUtils.copyProperties(spriteDto, newSprite);
                newSprite.setAttachment(spriteExists.get().getAttachment());
                newSprite.setImage(spriteExists.get().getImage());
                newSprite.setId(spriteExists.get().getId());
                newSprite.setCategory(category.get());
                newSprite.setSize(size.get());
                newSprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
                return ResponseEntity.status(HttpStatus.CREATED).body(spriteServiceImpl.saveSprite(newSprite));
            }
        }
        Optional<Attachment> attachment = attachmentRepository.findById(spriteDto.getAttachment_id());
        Optional<Image> image = imageRepository.findById(spriteDto.getImage_id());

        try {
            if (category.isPresent() && size.isPresent() && attachment.isPresent() && image.isPresent()) {
                if (spriteServiceImpl.existsBySlug(spriteDto.getSlug()).isPresent()) {
                    throw new ErrorControllerException("This product already exists!");
                }

                if (!spriteServiceImpl.exixstsByAttachment(spriteDto.getAttachment_id()).isPresent()) {
                    throw new ErrorControllerException("This product already exists!");
                }

                Sprite sprite = new Sprite();

                sprite.setActive(spriteDto.getActive());
                sprite.setIsUnique(spriteDto.getIsUnique());
                sprite.setDescription(spriteDto.getDescription());
                sprite.setName(spriteDto.getName());
                sprite.setPrice(spriteDto.getPrice());
                sprite.setSlug(spriteDto.getSlug());
                sprite.setAttachment(attachment.get());
                sprite.setCategory(category.get());
                sprite.setImage(image.get());
                sprite.setSize(size.get());
                sprite.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
                sprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

                return ResponseEntity.status(HttpStatus.CREATED).body(spriteServiceImpl.saveSprite(sprite));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("");
    }

    // DELETE SPRITE
    @PostMapping("/sprite/disabled/{id}")
    public ResponseEntity<Object> disabledSprite(@PathVariable(value = "id") Long id) {
        Optional<Sprite> sprite = spriteServiceImpl.findById(id);
        Sprite newSprite = new Sprite();

        if (sprite.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sprite not found!");
        }

        BeanUtils.copyProperties(sprite.get(), newSprite);
        newSprite.setActive(false);
        newSprite.setId(id);
        newSprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        spriteServiceImpl.saveSprite(newSprite);
        return ResponseEntity.status(HttpStatus.OK).body("Sprite disabled successfuly");
    }

    // PAYMENT
    @PostMapping("/payment/pay")
    public String payment(@RequestBody Order order) throws PayPalRESTException, IOException {
        String SUCCESS_URL_PARAMS = "/payment/public/pay/success" + "?idsSprites=" + order.getIdsSprites() + "&idUser=" + order.getIdUser();
        com.paypal.api.payments.Payment payment = paymentServiceImpl.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                order.getIntent(), order.getDescription(),"http://localhost:8081/admin" + CANCEL_URL,
                "http://localhost:8081/admin" + SUCCESS_URL_PARAMS);

        for(Links link:payment.getLinks()) {
            if(link.getRel().equals("approval_url")) {
                return link.getHref();
            }
        }

        return "";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000/cancel");
        return "cancel";
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam("idsSprites") String idsSpritesString,
            @RequestParam("idUser") Long idUser,
            HttpServletResponse response) throws IOException {
        try {
            com.paypal.api.payments.Payment payment = paymentServiceImpl.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                paymentServiceImpl.savePayment(payment, idsSpritesString, idUser);
                Optional<User> user = userServiceImpl.findByIdReturnUser(idUser);
                if (user.isPresent()) {
                    userServiceImpl.emailConfirmationPayment(user.get().getEmail());
                    response.sendRedirect("http://localhost:3000/?success="+paymentId);
                }
            }
        } catch (PayPalRESTException e) {

            response.sendRedirect("http://localhost:3000/?error="+paymentId);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("/fetch/user")
    public User findUser(Authentication authentication) {
        Optional<User> user = userServiceImpl.findByEmailReturnUser(authentication.getName());

        if (!user.isPresent()) {
            throw new ErrorLoginException("Account dont found!");
        }

        return user.get();
    }

    // FETCH PURCHASED PRODUCTS
    @GetMapping("/fetch/all/purchased")
    public ResponseEntity<Page<Payment>> fetchAllPurchased(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentServiceImpl.findAll(pageable));
    }

    // FETCH PAYMENTS OF USER
    @GetMapping("/fetch/payment/{id}")
    public ResponseEntity<Page<Payment>> fetchPaymentByUser(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentServiceImpl.findByUser(pageable, id));
    }

    // FETCH PRODUCT USER
    @GetMapping("/fetch/products/{id}")
    public ResponseEntity<Page<Sprite>> fetchAllProducts(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentServiceImpl.findSpriteByUser(pageable, id));
    }

    @GetMapping("/fetch/all/products")
    public ResponseEntity<Page<SpriteResponse>> fetchAllProducts(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("category_id") Long category_id,
            @RequestParam("size_id") Long size_id) {

        return ResponseEntity.status(HttpStatus.OK).body(spriteServiceImpl.fetchAllProductsByAdmin(pageable, category_id, size_id));
    }

    @GetMapping("/fetch/totalPage/product")
    public ResponseEntity<Object> fetchTotalPageProductByAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body(spriteServiceImpl.fetchTotalPageProductByAdmin());
    }

    // SEARCH PRODUCT BY NAME
    @GetMapping("/find/products")
    public ResponseEntity<Page<SpriteResponse>> findProducts(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("product") String product) {
        return ResponseEntity.status(HttpStatus.OK).body(spriteServiceImpl.findProductsByName(pageable, product));
    }


    @GetMapping("/fetch/categories/most/bought")
    public ResponseEntity<CategoriesBoughtResponse> fetchProductMostBought() {
        return ResponseEntity.status(HttpStatus.OK).body(paymentServiceImpl.fetchProductsMostBought());
    }

    // FETCH CATEGORIES MOST BOUGHT
}