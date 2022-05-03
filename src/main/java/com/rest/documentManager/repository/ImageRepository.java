package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
