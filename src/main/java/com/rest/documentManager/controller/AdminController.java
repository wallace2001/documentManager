package com.rest.documentManager.controller;

import com.rest.documentManager.dto.CategoryDto;
import com.rest.documentManager.dto.SizeDto;
import com.rest.documentManager.dto.SpriteDto;
import com.rest.documentManager.entity.*;
import com.rest.documentManager.repository.AttachmentRepository;
import com.rest.documentManager.repository.CategoryRepository;
import com.rest.documentManager.repository.ImageRepository;
import com.rest.documentManager.repository.SizeRepository;
import com.rest.documentManager.service.impl.ImageServiceImpl;
import com.rest.documentManager.service.impl.SpriteServiceImpl;
import com.rest.documentManager.services.exceptions.ErrorControllerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

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
    public ResponseEntity<Object> saveSprite(@RequestBody SpriteDto spriteDto) throws Exception {
        Optional<Category> category = categoryRepository.findById(spriteDto.getCategory_id());
        Optional<Size> size = sizeRepository.findById(spriteDto.getSize_id());
        Optional<Attachment> attachment = attachmentRepository.findById(spriteDto.getAttachment_id());
        Optional<Image> image = imageRepository.findById(spriteDto.getImage_id());

        if (category.isPresent() && size.isPresent() && attachment.isPresent() && image.isPresent()) {
            if (spriteServiceImpl.existsBySlug(spriteDto.getSlug()).isPresent()) {
                throw new ErrorControllerException("This product already exists!");
            }

            if (!spriteServiceImpl.exixstsByAttachment(spriteDto.getAttachment_id()).isPresent()) {
                throw new ErrorControllerException("This product already exists!");
            }

            Sprite sprite = new Sprite();
            BeanUtils.copyProperties(spriteDto, sprite);

            sprite.setAttachment(attachment.get());
            sprite.setCategory(category.get());
            sprite.setImage(image.get());
            sprite.setSize(size.get());
            sprite.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            sprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

            return ResponseEntity.status(HttpStatus.CREATED).body(spriteServiceImpl.saveSprite(sprite));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("");
    }

    // DELETE SPRITE
    @DeleteMapping("/sprite/delete/{id}")
    public ResponseEntity<Object> deleteSprite (@PathVariable(value = "id") Long id) {
        Optional<Sprite> sprite = spriteServiceImpl.findById(id);

        if (sprite.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sprite not found!");
        }

        spriteServiceImpl.delete(sprite.get());
        return ResponseEntity.status(HttpStatus.OK).body("Sprite deleted successfuly");
    }

    // FETCH PURCHASED PRODUCTS

    // FETCH CATEGORIES MOST BOUGHT

    // FETCH PURCHASED PRODUCT OF USER

    // FETCH ALL PAYMENTS

    // FETCH PAYMENTS OF USER
}
