package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Sprite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpriteRepository extends JpaRepository<Sprite, Long> {

    @Query("SELECT s FROM Sprite s WHERE s.slug = :slug")
    Optional<Sprite> existsBySlug(String slug);

    @Query("SELECT a FROM Attachment a WHERE a.id = :id")
    Optional<Attachment> existsByAttachment(Long id);

    @Query("SELECT s FROM Sprite s WHERE s.active = 1")
    Page<Sprite> findAll(Pageable pageable);

    @Query("SELECT s FROM Sprite s WHERE s.category.id = :category_id AND s.active = 1")
    Page<Sprite> findAllByCategory(Pageable pageable, Long category_id);

    @Query("SELECT s FROM Sprite s WHERE s.size.id = :size_id AND s.category.id = :category_id AND s.active = 1")
    Page<Sprite> findAllByCategoryAndSize(Pageable pageable, Long category_id, Long size_id);

    @Query("SELECT s FROM Sprite s")
    Page<Sprite> findAllAdmin(Pageable pageable);

    @Query("SELECT s FROM Sprite s WHERE s.category.id = :category_id")
    Page<Sprite> findAllByCategoryAdmin(Pageable pageable, Long category_id);

    @Query("SELECT s FROM Sprite s WHERE s.size.id = :size_id AND s.category.id = :category_id")
    Page<Sprite> findAllByCategoryAndSizeAdmin(Pageable pageable, Long category_id, Long size_id);

    @Query("SELECT s FROM Sprite s WHERE s.name LIKE :product%")
    Page<Sprite> findProducts(Pageable pageable, String product);

    Optional<Sprite> findById(Long id);

    @Query("SELECT s FROM Sprite s")
    List<Sprite> fetchAllProductsByAdmin();

    @Query("SELECT s FROM Sprite s WHERE s.active = 1")
    List<Sprite> fetchAllProductsByUser();

    void delete(Sprite id);
}
