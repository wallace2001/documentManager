package com.rest.documentManager.service;

import com.rest.documentManager.entity.Attachment;
import com.rest.documentManager.repository.AttachmentRepository;
import com.rest.documentManager.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AttachmentServiceImplTest {

    private static final String FILE_PATH = "D:\\Arrows.zip";
    private static final File FILE = new File(FILE_PATH);

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Mock
    private AttachmentRepository attachmentRepository;

    private Attachment attachment;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        startAttachment();
    }

    @Test
    void saveAttachment() throws Exception {
        when(attachmentRepository.save(any())).thenReturn(attachment);

        InputStream stream =  new FileInputStream(FILE);
        MultipartFile multipartFileToSend = new MockMultipartFile("file", FILE.getName(), MediaType.TEXT_HTML_VALUE, stream);

        Attachment response = attachmentService.saveAttachment(multipartFileToSend);

        assertNotNull(response);
        assertEquals(response.getClass(), Attachment.class);

        assertEquals(response.getId(), attachment.getId());
        assertEquals(response.getFileName(), attachment.getFileName());
        assertEquals(response.getData(), attachment.getData());
        assertEquals(response.getFileType(), attachment.getFileType());
    }

    @Test
    void whenSaveFileShouldReturnExcpetion() {
        when(attachmentRepository.save(any())).thenReturn(attachment);

        try {
            InputStream stream =  new FileInputStream(FILE);
            MultipartFile multipartFileToSend = new MockMultipartFile("file", ".." + FILE.getName(), MediaType.TEXT_HTML_VALUE, stream);

            attachmentService.saveAttachment(multipartFileToSend);
        } catch (Exception exception) {
            assertEquals(Exception.class, exception.getClass());
            assertEquals(exception.getMessage(), "Could not save File: .." + FILE.getName());
        }
    }

    @Test
    void whenGetAttachmentReturnDownload() throws Exception {
        when(attachmentRepository.findById(any())).thenReturn(Optional.ofNullable(attachment));

        Attachment response = attachmentService.getAttachment(1);

        assertNotNull(response);

        assertEquals(response.getClass(), Attachment.class);

        assertEquals(response.getData(), attachment.getData());
        assertEquals(response.getId(), attachment.getId());
        assertEquals(response.getFileType(), attachment.getFileType());
        assertEquals(response.getFileName(), attachment.getFileName());
    }

    @Test
    void whenGetAttachmentReturnNewExcpetion() {
        when(attachmentRepository.save(any())).thenReturn(attachment);

        try {
            attachmentService.getAttachment(2);
        } catch (Exception exception) {
            assertNotNull(exception);
            assertEquals(exception.getClass(), Exception.class);
            assertEquals(exception.getMessage(), "File not found with Id: " + 2);
        }
    }

    private void startAttachment() throws IOException {
        attachment = new Attachment(
                FILE.getName(),
                new MimetypesFileTypeMap().getContentType(FILE),
                Files.readAllBytes(FILE.toPath())
        );

        attachment.setId(1);
    }
}