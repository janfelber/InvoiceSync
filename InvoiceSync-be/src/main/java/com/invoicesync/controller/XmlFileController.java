package com.invoicesync.controller;

import com.invoicesync.dto.XmlFileResponseDto;
import com.invoicesync.service.XmlFileService;
import com.invoicesync.user.CurrentUserService;
import com.invoicesync.xml.processing.XMLProcessor;
import com.invoicesync.xml.service.XmlFileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/xml-file")
public class XmlFileController {

    private final XmlFileService xmlFileService;
    private final CurrentUserService currentUserService;
    private final XmlFileProcessingService xmlFileProcessingService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> saveXmlFile(@RequestParam("file") MultipartFile file) {
        try {
            xmlFileService.saveXmlFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/imports/current-user")
    public List<XmlFileResponseDto> getCurrentUserXmlFiles() {
        Long currentUserId = currentUserService.getCurrentUserId();
        List<XmlFileResponseDto> files = xmlFileService.getXmlFilesByUserId(currentUserId);
        files.sort((file1, file2) -> file2.getCreatedAt().compareTo(file1.getCreatedAt()));
        return files;
    }

    @GetMapping("/content/{importId}")
    public String getXmlContentByImportId(@PathVariable Long importId) {
        return xmlFileService.getXmlContentByImportId(importId);
    }

    @PostMapping("/generate-zip/{importId}")
    public ResponseEntity<byte[]> processAndGenerateZip(@PathVariable Long importId) {
        try {

            byte[] zipContent = xmlFileProcessingService.processAndGenerateZip(importId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("generated_xml.zip").build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Return the zip content as a byte array for download
            return ResponseEntity.ok().headers(headers).body(zipContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
