package com.samarthanam.digitallibrary.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
@Component
public class AWSCloudService {

    private static Logger logger = Logger.getLogger(AWSCloudService.class.getName());

    @Autowired
    private AmazonS3 amazonS3;

    /*@Value("${s3.buckek.name}")
    String defaultBucketName;

    @Value("${s3.default.folder}")
    String defaultBaseFolder;*/

    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }


    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public void uploadFile(final MultipartFile multipartFile) {


        System.out.println("file upload in progress");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket("samarthanampersonaldevelopment", file);
            logger.info("File upload is completed.");
            file.delete();
        } catch (final AmazonServiceException ex) {

            logger.severe("Error= {} while uploading file..." + ex.getMessage());
        }
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            logger.severe("Error converting the multi-part file to file= " + ex.getMessage());
            ex.printStackTrace();
        }
        return file;
    }

    private void uploadFileToS3Bucket(final String bucketName, final File file) {
        final String uniqueFileName = file.getName();
        logger.info("Uploading file with name= " + uniqueFileName);
        //System.out.println("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
    }


    public List<String> getObjects(final String bucketName) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        List<String> keyList = new ArrayList<String>();
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            keyList.add(os.getKey());
        }
        return keyList;
    }

    public String generatePresignedUrl(String fileName) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest("samarthanampersonaldevelopment", fileName, HttpMethod.GET)
                        .withExpiration(Date.from(LocalDateTime.now()
                                                          .plusMinutes(60l)
                                                          .atZone(ZoneId.of("Asia/Kolkata"))
                                                          .toInstant()));
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }


}


