package com.rest.documentManager.controller;

import com.rest.documentManager.dto.CategoryDto;
import com.rest.documentManager.dto.SizeDto;
import com.rest.documentManager.dto.SpriteDto;
import com.rest.documentManager.entity.*;
import com.rest.documentManager.repository.AttachmentRepository;
import com.rest.documentManager.repository.CategoryRepository;
import com.rest.documentManager.repository.ImageRepository;
import com.rest.documentManager.repository.SizeRepository;
import com.rest.documentManager.response.*;
import com.rest.documentManager.service.SpriteService;
import com.rest.documentManager.service.impl.*;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    private ImageRepository imageRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private SpriteService spriteService;

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
    public ResponseEntity<Object> saveSprite(@RequestBody SpriteDto spriteDto, HttpServletResponse response) throws Exception {
        Optional<Sprite> spriteExists = spriteService.findById(spriteDto.getId());
        Optional<Category> category = categoryRepository.findById(spriteDto.getCategory_id());
        Optional<Size> size = sizeRepository.findById(spriteDto.getSize_id());

        if (spriteExists.isPresent()) {
            if (category.isPresent() && size.isPresent()) {
                Sprite newSprite = new Sprite();
                BeanUtils.copyProperties(spriteDto, newSprite);
                newSprite.setImage(spriteExists.get().getImage());
                newSprite.setId(spriteExists.get().getId());
                newSprite.setCategory(category.get());
                newSprite.setSize(size.get());
                newSprite.setCreatedAt(spriteExists.get().getCreatedAt());
                newSprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
                return ResponseEntity.status(HttpStatus.CREATED).body(spriteService.saveSprite(newSprite));
            }
        }
        Optional<Image> image = imageRepository.findById(spriteDto.getImage_id());

        try {
            if (category.isPresent() && size.isPresent() && image.isPresent()) {
                if (spriteService.existsBySlug(spriteDto.getSlug()).isPresent()) {
                    throw new ErrorControllerException("This product already exists!");
                }

                Sprite sprite = new Sprite();

                sprite.setActive(spriteDto.getActive());
                sprite.setIsUnique(spriteDto.getIsUnique());
                sprite.setDescription(spriteDto.getDescription());
                sprite.setName(spriteDto.getName());
                sprite.setPrice(spriteDto.getPrice());
                sprite.setSlug(spriteDto.getSlug());
                sprite.setCategory(category.get());
                sprite.setImage(image.get());
                sprite.setSize(size.get());
                sprite.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
                sprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

                return ResponseEntity.status(HttpStatus.CREATED).body(spriteService.saveSprite(sprite));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("");
    }

    // DELETE SPRITE
    @PostMapping("/sprite/disabled/{id}")
    public ResponseEntity<Object> disabledSprite(@PathVariable(value = "id") Long id) {
        Optional<Sprite> sprite = spriteService.findById(id);

        if (sprite.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sprite not found!");
        }

        spriteService.delete(sprite.get());
        return ResponseEntity.status(HttpStatus.OK).body("Sprite disabled successfuly");
    }

    @GetMapping("/fetch/user")
    public User findUser(Authentication authentication) {
        Optional<User> user = userServiceImpl.findByEmailReturnUser(authentication.getName());

        if (!user.isPresent()) {
            throw new ErrorLoginException("Account dont found!");
        }

        return user.get();
    }

    // FETCH UNIQUE PRODUCT
    @GetMapping("/fetch/product/{id}")
    public ResponseEntity<Object> fetchAllProducts(@PathVariable("id") Long id) {
        Optional<Sprite> sprite = spriteService.findById(id);

        if (sprite.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(sprite.get());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro ao encontrar Sprite!");
    }

    // FETCH PRODUCT USER

    @GetMapping("/fetch/all/products")
    public ResponseEntity<Page<SpriteResponse>> fetchAllProducts(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("category_id") Long category_id,
            @RequestParam("size_id") Long size_id) {

        return ResponseEntity.status(HttpStatus.OK).body(spriteService.fetchAllProductsByAdmin(pageable, category_id, size_id));
    }

    @GetMapping("/fetch/totalPage/product")
    public ResponseEntity<Object> fetchTotalPageProductByAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body(spriteService.fetchTotalPageProductByAdmin());
    }

    // SEARCH PRODUCT BY NAME
    @GetMapping("/find/products")
    public ResponseEntity<Page<SpriteResponse>> findProducts(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("product") String product) {
        return ResponseEntity.status(HttpStatus.OK).body(spriteService.findProductsByName(pageable, product));
    }
}