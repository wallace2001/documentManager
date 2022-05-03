package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Sprite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface SpriteRepository extends JpaRepository<Sprite, Long> {

    @Query("SELECT s FROM Sprite s WHERE s.slug = :slug")
    Optional<Sprite> existsBySlug(String slug);

    @Query("SELECT a FROM Attachment a WHERE a.id = :id")
    Optional<Attachment> existsByAttachment(Integer id);

    @Query("SELECT s FROM Sprite s")
    Page<Sprite> findAll(Pageable pageable);

    @Query("SELECT s FROM Sprite s WHERE s.category.id = :category_id")
    Page<Sprite> findAllByCategory(Pageable pageable, Long category_id);

    @Query("SELECT s FROM Sprite s WHERE s.size.id = :size_id AND s.category.id = :category_id")
    Page<Sprite> findAllByCategoryAndSize(Pageable pageable, Long category_id, Long size_id);

    Optional<Sprite> findById(Long id);

    void delete(Sprite id);
}
