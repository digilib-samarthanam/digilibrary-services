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

    @GetMapping(path = "/downloadPDF")
    public ResponseEntity<ByteArrayResource> downloadPDFFile(@RequestParam(value = "file") String file) throws IOException {
        byte[] data = service.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/pdf")
                // .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);

    }

    @GetMapping(path = "/downloadAudio")
    public ResponseEntity<ByteArrayResource> downloadAudioFile(@RequestParam(value = "file") String file) throws IOException {
        byte[] data = service.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                // .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);

    }


}