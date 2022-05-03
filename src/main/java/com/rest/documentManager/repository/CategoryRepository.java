package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findById(Category category_id);
}
