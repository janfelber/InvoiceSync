package com.invoicesync.service;

import com.google.api.Authentication;
import com.invoicesync.dto.XmlFileRequestDto;
import com.invoicesync.dto.XmlFileResponseDto;
import com.invoicesync.module.XmlFile;
import com.invoicesync.repository.UserCredentialRepository;
import com.invoicesync.repository.XmlFileRepository;
import com.invoicesync.user.UserDemo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class XmlFileServiceImpl implements XmlFileService {

    private final XmlFileRepository xmlFileRepository;

    @Override
    public XmlFile saveXmlFile(MultipartFile file) throws IOException {
        XmlFile xmlFile = new XmlFile();
        String filename = file.getOriginalFilename();
        String xmlContent = new String(file.getBytes());

        xmlFile.setUser((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        xmlFile.setFile_name(filename);
        xmlFile.setXml_content(xmlContent);
        xmlFile.setCreated_at(Date.from(new Date().toInstant()));
        return xmlFileRepository.save(xmlFile);
    }

    @Override
    public List<XmlFileResponseDto> getXmlFilesByUserId(Long userId) {
        return xmlFileRepository.findByUserId(userId)
                .stream()
                .map(xmlFile -> new XmlFileResponseDto(
                        xmlFile.getId(),
                        xmlFile.getFile_name(),
                        xmlFile.getCreated_at()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String getXmlContentByImportId(Long importId) {
        return xmlFileRepository.findById(importId)
                .map(XmlFile::getXml_content)
                .orElseThrow(() -> new IllegalArgumentException("Xml file not found"));
    }
}
