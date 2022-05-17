package com.rest.documentManager.service.impl;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Payment;
import com.rest.documentManager.entity.Sprite;
import com.rest.documentManager.repository.SpriteRepository;
import com.rest.documentManager.response.CategoriesBoughtResponse;
import com.rest.documentManager.response.CategoryInstance;
import com.rest.documentManager.response.SpriteResponse;
import com.rest.documentManager.service.SpriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SpriteServiceImpl implements SpriteService {

    @Autowired
    private SpriteRepository spriteRepository;


    @Override
    public Sprite saveSprite(Sprite sprite) {
        return spriteRepository.save(sprite);
    }

    @Override
    public Optional<Sprite> existsBySlug(String slug) {
        return spriteRepository.existsBySlug(slug);
    }

    @Override
    public Optional<Attachment> exixstsByAttachment(Long id) {
        return spriteRepository.existsByAttachment(id);
    }

    @Override
    public Page<SpriteResponse> fetchAllProducts(Pageable pageable, Long category_id, Long size_id) {
        List<SpriteResponse> spritesFiltered = new ArrayList<>();
        Page<Sprite> sprites = null;

        if (category_id != 0 && size_id == 0) {
             sprites = spriteRepository.findAllByCategory(pageable, category_id);
        } else if (category_id != 0 && size_id != 0) {
            sprites = spriteRepository.findAllByCategoryAndSize(pageable, category_id, size_id);
        } else {
            sprites = spriteRepository.findAll(pageable);
        }

        for (Sprite sprite : sprites) {
            SpriteResponse spriteResponse = new SpriteResponse(
                    sprite.getId(),
                    sprite.getName(),
                    sprite.getPrice(),
                    sprite.getSlug(),
                    sprite.getIsUnique(),
                    sprite.getActive(),
                    sprite.getCategory(),
                    sprite.getSize(),
                    sprite.getImage(),
                    sprite.getCreatedAt(),
                    sprite.getUpdatedAt(),
                    sprite.getDescription()
            );

            spritesFiltered.add(spriteResponse);
        }
        return new PageImpl<>(spritesFiltered);
    }

    @Override
    public Optional<Sprite> findById(Long id) {
        return spriteRepository.findById(id);
    }

    @Override
    public void delete(Sprite sprite) {
        spriteRepository.delete(sprite);
    }

    @Override
    public Page<SpriteResponse> fetchAllProductsByAdmin(Pageable pageable, Long category_id, Long size_id) {
        List<SpriteResponse> spritesFiltered = new ArrayList<>();
        Page<Sprite> sprites = null;

        if (category_id != 0 && size_id == 0) {
            sprites = spriteRepository.findAllByCategoryAdmin(pageable, category_id);
        } else if (category_id != 0 && size_id != 0) {
            sprites = spriteRepository.findAllByCategoryAndSizeAdmin(pageable, category_id, size_id);
        } else {
            sprites = spriteRepository.findAllAdmin(pageable);
        }

        for (Sprite sprite : sprites) {
            SpriteResponse spriteResponse = new SpriteResponse(
                    sprite.getId(),
                    sprite.getName(),
                    sprite.getPrice(),
                    sprite.getSlug(),
                    sprite.getIsUnique(),
                    sprite.getActive(),
                    sprite.getCategory(),
                    sprite.getSize(),
                    sprite.getImage(),
                    sprite.getCreatedAt(),
                    sprite.getUpdatedAt(),
                    sprite.getDescription()
            );

            spritesFiltered.add(spriteResponse);
        }
        return new PageImpl<>(spritesFiltered);
    }

    @Override
    public Page<SpriteResponse> findProductsByName(Pageable pageable, String product) {
        List<SpriteResponse> spritesFiltered = new ArrayList<>();
        Page<Sprite> sprites = spriteRepository.findProducts(pageable, product);
        for (Sprite sprite : sprites) {
            SpriteResponse spriteResponse = new SpriteResponse(
                    sprite.getId(),
                    sprite.getName(),
                    sprite.getPrice(),
                    sprite.getSlug(),
                    sprite.getIsUnique(),
                    sprite.getActive(),
                    sprite.getCategory(),
                    sprite.getSize(),
                    sprite.getImage(),
                    sprite.getCreatedAt(),
                    sprite.getUpdatedAt(),
                    sprite.getDescription()
            );

            spritesFiltered.add(spriteResponse);
        }
        return new PageImpl<>(spritesFiltered);
    }

    @Override
    public Number fetchTotalPageProductByAdmin() {
        return spriteRepository.fetchAllProductsByAdmin().size();
    }


    @Override
    public Number fetchTotalPageProductByUser() {
        return spriteRepository.fetchAllProductsByUser().size();
    }
}
