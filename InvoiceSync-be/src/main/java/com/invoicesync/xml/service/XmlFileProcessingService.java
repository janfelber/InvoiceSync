package com.invoicesync.xml.service;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.springframework.stereotype.Service;

import com.invoicesync.xml.processing.XMLProcessor;
import com.invoicesync.xmlFile.XmlFileService;

import lombok.AllArgsConstructor;

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
    public byte[] processAndGenerateZip(final Long importId) throws Exception {

        final String xmlContent = xmlFileService.getXmlContentByImportId(importId);

        final File templateFile = new File("src/main/resources/schema.xml");

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        XMLProcessor.processFile(xmlContent, templateFile, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
