package com.samarthanam.digitallibrary.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.samarthanam.digitallibrary.service.AWSCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${s3.bucket.name}")
    private String bucketName;

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

    @GetMapping(value = "/files/{bucket_name}")
    public List<String> getBucketFilesInAFolder(@PathVariable("bucket_name") String bucketName, @RequestParam("folder_name") String folderRelativePath) {
        return service.getFiles(bucketName, folderRelativePath);
    }

    @GetMapping(path = "/download_url")
    public String getDownloadUrl(@RequestParam(value = "file_name") String fileName) {
        return service.generatePresignedUrl(fileName);
    }

    @GetMapping(value = "/get_bulk_upload_files")
    public List<String> getBulkUploadFiles() {
        return service.getFiles(bucketName, "bulk_upload_books");
    }


}
