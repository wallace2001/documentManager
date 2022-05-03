package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Size findById(Size size_id);
}
