package com.rest.documentManager.service.impl;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.rest.documentManager.entity.Image;
import com.rest.documentManager.repository.ImageRepository;
import com.rest.documentManager.service.ImageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Log
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${application.bucket.endpoint}")
    private String endpointAmazon;

    @Override
    public Image saveImage(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }
            File fileObj = convertMultiPartFileToFile(file);
            String fileNameObj = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            log.info(fileNameObj);
            amazonS3.putObject(new PutObjectRequest(bucketName, fileNameObj, fileObj));
            String fileUrl = endpointAmazon + "/" + fileName;
            fileObj.delete();

            Image image = new Image(fileUrl);
            return imageRepository.save(image);
        } catch (Exception err) {
            throw new Exception("Could not save File: " + fileName);
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.info("Error converting multipartFile to file");
        }
        return convertedFile;
    }
}
