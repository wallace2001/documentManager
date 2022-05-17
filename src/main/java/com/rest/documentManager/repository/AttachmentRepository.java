package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findById(Long attachment_id);
}
