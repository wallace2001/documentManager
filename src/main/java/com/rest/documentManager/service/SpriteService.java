package com.rest.documentManager.service;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Sprite;
import com.rest.documentManager.response.SpriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SpriteService {
    Sprite saveSprite(Sprite sprite);

    Optional<Sprite> existsBySlug(String slug);

    Optional<Attachment> exixstsByAttachment(Integer id);

    Page<SpriteResponse> fetchAllProducts(Pageable pageable, Long category_id, Long size_id);

    Optional<Sprite> findById(Long id);

    void delete(Sprite id);
}
