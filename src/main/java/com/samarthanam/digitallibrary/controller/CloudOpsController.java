package com.samarthanam.digitallibrary.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.samarthanam.digitallibrary.service.AWSCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
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
    public ResponseEntity<List> getBucketObjects(@PathVariable("bucket_name") String bucket_name) {
        List<String> objectList = service.getObjects(bucket_name);

        return new ResponseEntity<>(objectList, HttpStatus.OK);
    }

    @GetMapping(path = "/download")
    public String downloadFile(@RequestParam(value = "file_name") String fileName) {
        return service.generatePresignedUrl(fileName);
    }


}