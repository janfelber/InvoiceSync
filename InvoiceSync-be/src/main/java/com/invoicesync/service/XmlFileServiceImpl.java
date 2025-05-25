package com.invoicesync.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.XmlFileResponseDto;
import com.invoicesync.module.XmlFile;
import com.invoicesync.repository.XmlFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XmlFileServiceImpl implements XmlFileService {

    private final XmlFileRepository xmlFileRepository;

    @Override
    public XmlFile saveXmlFile(MultipartFile file) throws IOException {
        // XmlFile xmlFile = new XmlFile();
        // String filename = file.getOriginalFilename();
        // String xmlContent = new String(file.getBytes());
        //
        // xmlFile.setUser((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // xmlFile.setFile_name(filename);
        // xmlFile.setXml_content(xmlContent);
        // xmlFile.setCreated_at(Date.from(new Date().toInstant()));
        // return xmlFileRepository.save(xmlFile);

        return null;
    }

    @Override
    public List<XmlFileResponseDto> getXmlFilesByUserId(Long userId) {
        // return xmlFileRepository.findByUserId(userId)
        //         .stream()
        //         .map(xmlFile -> new XmlFileResponseDto(
        //                 xmlFile.getId(),
        //                 xmlFile.getFile_name(),
        //                 xmlFile.getCreated_at()
        //         ))
        //         .collect(Collectors.toList());
        //
        return null;
    }

    @Override
    public String getXmlContentByImportId(Long importId) {
        return xmlFileRepository.findById(importId)
                .map(XmlFile::getXml_content)
                .orElseThrow(() -> new IllegalArgumentException("Xml file not found"));
    }
}
