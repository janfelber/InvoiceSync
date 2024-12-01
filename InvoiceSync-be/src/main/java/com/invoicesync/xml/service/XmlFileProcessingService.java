package com.invoicesync.xml.service;

import com.invoicesync.service.XmlFileService;
import com.invoicesync.xml.processing.XMLProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
@AllArgsConstructor
public class XmlFileProcessingService {

    private XmlFileService xmlFileService;

    /**
     * Get XML content by import ID and generate ZIP content
     *
     * @param importId Import ID
     * @return ZIP content
     * @throws Exception If an error occurs
     */
    public byte[] processAndGenerateZip(Long importId) throws Exception {

        String xmlContent = xmlFileService.getXmlContentByImportId(importId);

        File templateFile = new File("src/main/resources/schema.xml");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        XMLProcessor.processFile(xmlContent, templateFile, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
