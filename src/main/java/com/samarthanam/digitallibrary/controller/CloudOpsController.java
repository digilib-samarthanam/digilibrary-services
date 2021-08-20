package com.samarthanam.digitallibrary.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.samarthanam.digitallibrary.service.AWSCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CloudOpsController {

    @Autowired
    private AWSCloudService service;

    @GetMapping(path = "/buckets")
    public List<Bucket> listBuckets() {
        return service.getAllBuckets();
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") final MultipartFile multipartFile) {

        service.uploadFile(multipartFile);
        final String response = "[" + multipartFile.getOriginalFilename() + "] uploaded successfully.";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/buckets/{bucket_name}")
    public List<String> getBucketObjects(@PathVariable("bucket_name") String bucketName) {
        return service.getObjects(bucketName);
    }

    @GetMapping(path = "/download_url")
    public String getDownloadUrl(@RequestParam(value = "file_name") String fileName) {
        return service.generatePresignedUrl(fileName);
    }


}
