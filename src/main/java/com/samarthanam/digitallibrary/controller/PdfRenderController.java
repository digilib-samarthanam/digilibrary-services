package com.samarthanam.digitallibrary.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PdfRenderController {

    @GetMapping(value = "books/v1/pdfs/{pdf_book_id}" ,  produces = "application/pdf")
    public ResponseEntity<InputStreamResource> renderPdf() throws IOException {

        ClassPathResource pdfFile = new ClassPathResource("TaxProof_UserManual_Upload.pdf");
        return ResponseEntity
                .ok()
                .contentLength(pdfFile.contentLength())
                .contentType(
                        MediaType.parseMediaType("application/pdf"))
                .body(new InputStreamResource(pdfFile.getInputStream()));

    }

    @GetMapping(value = "books/v1/audios/{audio_book_id}" ,  produces = "application/json")
    public ResponseEntity<InputStreamResource> renderAudio() throws IOException {

         return null;
        // TO-DO Implementation
    }
}
