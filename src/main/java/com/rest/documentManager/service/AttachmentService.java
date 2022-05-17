package com.rest.documentManager.service;

import com.rest.documentManager.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file) throws Exception;

    Attachment getAttachment(Long id) throws Exception;
}
