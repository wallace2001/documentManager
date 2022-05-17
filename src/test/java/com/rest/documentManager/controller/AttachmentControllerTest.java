package com.rest.documentManager.controller;

import com.rest.documentManager.response.ResponseData;
import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
class AttachmentControllerTest {

    private static final String FILE_PATH = "D:\\Arrows.zip";
    private static final File FILE = new File(FILE_PATH);

    private Attachment attachment = new Attachment();

    @InjectMocks
    private AttachmentController attachmentController;

    @Mock
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        startAttachment();
    }

    @Test
    void withUploadFileReturnSuccess() throws Exception {
        when(attachmentService.saveAttachment(any())).thenReturn(attachment);

        InputStream stream =  new FileInputStream(FILE);
        MultipartFile multipartFileToSend = new MockMultipartFile("file", FILE.getName(), MediaType.TEXT_HTML_VALUE, stream);
        ResponseData result = attachmentController.uploadFile(multipartFileToSend);

        String downloadURL = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId().toString())
                .toUriString();

        assertNotNull(result);

        assertEquals(result.getClass(), ResponseData.class);

        assertEquals(result.getFileName(), FILE.getName());
        assertEquals(result.getFileSize(), multipartFileToSend.getSize());
        assertEquals(result.getDownloadURL(), downloadURL);
        assertEquals(result.getFileType(), multipartFileToSend.getContentType());
    }

//    @Test
//    void withDownloadReturnFile() throws Exception {
//        when(attachmentService.getAttachment(any())).thenReturn(attachment);
//
//        ResponseEntity<Object> response = attachmentController.downloadFile(Long.valueOf(1));
//
//        assertNotNull(response);
//        assertNotNull(response.getBody());
//
//        assertEquals(response.getBody().getClass(), ByteArrayResource.class);
//        assertEquals(response.getClass(), ResponseEntity.class);
//
//        assertEquals(response.getStatusCodeValue(), 200);
//    }

    private void startAttachment() throws IOException {
        attachment = new Attachment(
                FILE.getName(),
                new MimetypesFileTypeMap().getContentType(FILE),
                Files.readAllBytes(FILE.toPath())
        );

        attachment.setId(1L);
    }
}