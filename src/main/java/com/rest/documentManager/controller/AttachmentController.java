package com.rest.documentManager.controller;

import com.rest.documentManager.entity.Image;
import com.rest.documentManager.response.ResponseData;
import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.service.AttachmentService;
import com.rest.documentManager.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/file")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ImageServiceImpl imageServiceImpl;


    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

        Attachment attachment = null;
        String downloadURL = "";
        attachment = attachmentService.saveAttachment(file);
        downloadURL = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId().toString())
                .toUriString();

        return new ResponseData(attachment.getFileName(), downloadURL, file.getContentType(), file.getSize());
    }

    @PostMapping("/upload/image")
    public ResponseData uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        Image attachmentImage = null;
        String downloadURL = "";
        attachmentImage = imageServiceImpl.saveImage(file);
        downloadURL = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/download/")
                .path(attachmentImage.getId().toString())
                .toUriString();
        return new ResponseData("Image", downloadURL, file.getContentType(), file.getSize());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) throws Exception {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
