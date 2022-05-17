package com.rest.documentManager.service;

import com.rest.documentManager.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Image saveImage(MultipartFile file) throws Exception;
}
