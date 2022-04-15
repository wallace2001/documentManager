package com.rest.documentManager.controller;

import com.rest.documentManager.ResponseData;
import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

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

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) throws Exception {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
