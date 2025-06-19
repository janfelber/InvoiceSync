package com.invoicesync.xmlFile;

import static com.invoicesync.xmlFile.specification.XmlSpecification.withUserId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.xmlFile.dto.XmlFileRequest;
import com.invoicesync.xmlFile.dto.XmlFileResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XmlFileServiceImpl implements XmlFileService {

    private final XmlFileRepository xmlFileRepository;

    private final XmlMapper xmlMapper;

    @Override
    public Long saveXmlFile(final MultipartFile file, final Authentication connectedUser) {
        try {
            final String filename = file.getOriginalFilename();
            final String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            final XmlFileRequest request = new XmlFileRequest(filename, xmlContent);
            final XmlFile xmlFile = xmlMapper.toXmlFile(request);
            return xmlFileRepository.save(xmlFile).getId();
        } catch (IOException e) {
            throw new RuntimeException("Nepodarilo sa načítať súbor: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public PageResponse<XmlFileResponseDto> finalAllXmlImportsByUser(final int page, final int size,
        final Authentication connectedUser) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        final Page<XmlFile> xmlFiles = xmlFileRepository.findAll(withUserId(connectedUser.getName()), pageable);

        final List<XmlFileResponseDto> xmlFileResponse = xmlFiles.stream()
            .map(xmlMapper::toXmlTableResponse)
            .toList();
        return new PageResponse<>(
            xmlFileResponse,
            xmlFiles.getNumber(),
            xmlFiles.getSize(),
            xmlFiles.getTotalElements(),
            xmlFiles.getTotalPages(),
            xmlFiles.isFirst(),
            xmlFiles.isLast()
        );
    }

    @Override
    public String getXmlContentByImportId(Long importId) {
        return xmlFileRepository.findById(importId)
                .map(XmlFile::getXml_content)
                .orElseThrow(() -> new IllegalArgumentException("Xml file not found"));
    }

}
