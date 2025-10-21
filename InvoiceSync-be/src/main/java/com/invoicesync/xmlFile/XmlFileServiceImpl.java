package com.invoicesync.xmlFile;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XmlFileServiceImpl implements XmlFileService {

    // private final XmlFileRepository xmlFileRepository;
    //
    // private final XmlMapper xmlMapper;
    //
    // private AuthenticationService user;
    //
    // @Override
    // public Long saveXmlFile(final MultipartFile file, final Authentication connectedUser) {
    //
    //     if (user.hasFeature(connectedUser, Feature.EKON_SPECIALTY)) {
    //         throw new FeatureMissingException("User is missing" + Feature.EKON_SPECIALTY + " feature");
    //     }
    //
    //     try {
    //         final String filename = file.getOriginalFilename();
    //         final String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
    //
    //         final XmlFileRequest request = new XmlFileRequest(filename, xmlContent);
    //         final XmlFile xmlFile = xmlMapper.toXmlFile(request);
    //         return xmlFileRepository.save(xmlFile).getId();
    //     } catch (IOException e) {
    //         throw new RuntimeException("Nepodarilo sa načítať súbor: " + file.getOriginalFilename(), e);
    //     }
    // }
    //
    // @Override
    // public PageResponse<XmlFileResponseDto> finalAllXmlImportsByUser(final int page, final int size,
    //     final Authentication connectedUser) {
    //
    //     if (user.hasFeature(connectedUser, Feature.EKON_SPECIALTY)) {
    //         throw new FeatureMissingException("User is missing" + Feature.EKON_SPECIALTY + " feature");
    //     }
    //
    //     final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    //     final Page<XmlFile> xmlFiles = xmlFileRepository.findAll(withUserId(connectedUser.getName()), pageable);
    //
    //     final List<XmlFileResponseDto> xmlFileResponse = xmlFiles.stream()
    //         .map(xmlMapper::toXmlTableResponse)
    //         .toList();
    //     return new PageResponse<>(
    //         xmlFileResponse,
    //         xmlFiles.getNumber(),
    //         xmlFiles.getSize(),
    //         xmlFiles.getTotalElements(),
    //         xmlFiles.getTotalPages(),
    //         xmlFiles.isFirst(),
    //         xmlFiles.isLast()
    //     );
    // }
    //
    // @Override
    // public String getXmlContentByImportId(Long importId) {
    //
    //     return xmlFileRepository.findById(importId)
    //             .map(XmlFile::getXml_content)
    //             .orElseThrow(() -> new IllegalArgumentException("Xml file not found"));
    // }

}
