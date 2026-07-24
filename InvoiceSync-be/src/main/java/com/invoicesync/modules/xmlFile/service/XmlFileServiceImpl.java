package com.invoicesync.modules.xmlFile.service;

import static com.invoicesync.modules.xmlFile.specification.XmlSpecification.withUserId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.modules.auth.security.OwnedXML;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.modules.xml.processing.XMLProcessor;
import com.invoicesync.modules.xmlFile.dto.XmlFileRequest;
import com.invoicesync.modules.xmlFile.dto.XmlFileResponseDto;
import com.invoicesync.modules.xmlFile.mapper.XmlMapper;
import com.invoicesync.modules.xmlFile.model.XmlFile;
import com.invoicesync.modules.xmlFile.repository.XmlFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XmlFileServiceImpl implements XmlFileService {

  private final XmlFileRepository xmlFileRepository;

  private final XmlMapper xmlMapper;

  private final UserRepository userRepository;

  @Override
  public Long saveXmlFile(final MultipartFile file, final Authentication connectedUser) {
    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    if (!user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      throw new IllegalStateException("User does not have " + FeatureEnum.EKON_SPECIALTY + " feature.");
    }

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

    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    if (!user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      throw new IllegalStateException("User does not have " + FeatureEnum.EKON_SPECIALTY + " feature.");
    }

    final Pageable pageable = PageableFactory.ofDescending(page, size);
    final Page<XmlFile> xmlFiles = xmlFileRepository.findAll(withUserId(connectedUser.getName()), pageable);

    return PageResponse.from(xmlFiles, xmlMapper::toXmlTableResponse);
  }

  /**
   * Get XML content by import ID and generate ZIP content
   *
   * @param importId Import ID
   * @return ZIP content
   * @throws Exception If an error occurs
   */
  @Override
  @RequiresOwnership
  public byte[] processAndGenerateZip(final @OwnedXML Long importId, final Authentication connectedUser)
      throws Exception {

    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    if (!user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      throw new IllegalStateException("User does not have " + FeatureEnum.EKON_SPECIALTY + " feature.");
    }

    final String xmlContent = xmlFileRepository.findById(importId)
        .map(XmlFile::getXmlContent)
        .orElseThrow(() -> new IllegalArgumentException("Xml file not found"));

    final File templateFile = new File("src/main/resources/schema.xml");

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    XMLProcessor.processFile(xmlContent, templateFile, byteArrayOutputStream);

    return byteArrayOutputStream.toByteArray();
  }

}
