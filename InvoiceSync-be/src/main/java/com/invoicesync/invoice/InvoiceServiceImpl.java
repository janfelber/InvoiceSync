package com.invoicesync.invoice;

import static com.invoicesync.invoice.InvoiceSpecification.withCompanyId;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.company.CompanyRepository;
import com.invoicesync.invoice.dto.InvoiceImportResponseDto;
import com.invoicesync.invoice.dto.InvoiceResponseDTO;
import com.invoicesync.ocr.OCRService;
import com.invoicesync.shared.common.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;

  private final CompanyRepository companyRepository;

  private final OCRService ocrService;

  private final InvoiceMapper invoiceMapper;

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Override
  public List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId) {
    // return invoiceImportRepository.findByUserId(userId)
    //     .stream()
    //     .map(invoiceImport -> new InvoiceImportResponseDto(
    //         invoiceImport.getId(),
    //         invoiceImport.getImport_date(),
    //         new InvoiceResponseDetailsDTO(
    //             invoiceImport.getInvoice_number(),
    //             invoiceImport.getVariable_symbol(),
    //             invoiceImport.getVariable_symbol(),
    //             invoiceImport.getIssue_date(),
    //             invoiceImport.getTax_date(),
    //             invoiceImport.getDue_date()
    //         ),
    //         new PartnerDTO(
    //             invoiceImport.getPartner_name(),
    //             invoiceImport.getPartner_city(),
    //             invoiceImport.getPartner_street(),
    //             invoiceImport.getPartner_zip(),
    //             invoiceImport.getPartner_registration_number(),
    //             invoiceImport.getPartner_tax_id(),
    //             invoiceImport.getPartner_vat_id()
    //         ),
    //         invoiceImport.getStatus(),
    //         invoiceImport.getCompany().getId()
    //     ))
    //     .collect(Collectors.toList());

    return null;
  }

  @Override
  public InvoiceImportResponseDto getInvoiceById(final Long id) {
    return null;
  }

  // @Override
  // public Long saveInvoice(final MultipartFile file, final Authentication connectedUser) {
  //
  //   try {
  //     /* 1. OCR */
  //     final String extractedText = ocrService.extractTextFromPDF(file, connectedUser);
  //
  //     /* 2. Prompt */
  //     final String prompt = """
  //         Extract the following fields from the invoice text and return them in JSON format:
  //         - Invoice Number
  //         - Date of Issue
  //         - Date of Delivery
  //         - Variable Symbol
  //         - Supplier VAT ID
  //         - Supplier Registration Number
  //         - Invoice Items (description, quantity, unit price, total)
  //
  //         Invoice text:
  //         """ + extractedText;
  //
  //     final Map<String, Object> requestBody = Map.of(
  //         "model", "gpt-4o-mini",
  //         "messages", List.of(
  //             Map.of(
  //                 "role", "user",
  //                 "content", prompt
  //             )
  //         )
  //     );
  //
  //     /* 3. Volanie OpenAI a získanie JSON reťazca (blokujúco) */
  //     final InvoiceController.OpenAiResponse llmResponse = openAiClient.post()
  //         .uri("/chat/completions")
  //         .header("Authorization", "Bearer " + openAiApiKey)
  //         .header("Content-Type", "application/json")
  //         .bodyValue(requestBody)
  //         .retrieve()
  //         .bodyToMono(InvoiceController.OpenAiResponse.class)
  //         .block();                               //  ← blokujeme v servise
  //
  //     if (llmResponse == null || llmResponse.getChoices().isEmpty()) {
  //       throw new IllegalStateException("Empty response from OpenAI");
  //     }
  //
  //     final String json = llmResponse.getChoices().get(0).getMessage().getContent();
  //
  //     /* 4. Parse JSON → DTO */
  //     final InvoiceDto dto = objectMapper.readValue(json, InvoiceDto.class);
  //
  //     /* 5. DTO → Entity a persist */
  //     final Invoice entity = mapToEntity(dto, connectedUser);
  //     invoiceRepository.save(entity);
  //
  //     return entity.getId();
  //
  //   } catch (Exception e) {
  //     // môžeš zalogovať a hodiť vlastnú výnimku
  //     throw new RuntimeException("Invoice processing failed", e);
  //   }
  // }

  // final InvoiceImport invoiceImport = new InvoiceImport();
    // final String ocrText = ocrService.extractTextFromPDF(file);
    // final String pdfName = file.getOriginalFilename();
    // final Long userId = ((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    //
    // final String uploadDir = "src/uploads/users/" + userId;
    // final Path targetPath = Path.of(uploadDir, pdfName);
    //
    // Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    //
    // final Company company = companyRepository.findById(companyId)
    //     .orElseThrow(() -> new RuntimeException("Company not found"));
    //
    // invoiceImport.setUser((UserDemo) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    // final String vatId = ocrService.findVatId(ocrText);
    // final String iban = ocrService.findIban(ocrText);
    // final String ico = ocrService.findIco(ocrText);
    // final String dic = ocrService.findDic(ocrText);
    // final String dueDateStr = ocrService.findDueDate(ocrText);
    // final String issueDateStr = ocrService.findIssueDate(ocrText);
    // final String deliveryDateStr = ocrService.findDeliveryDate(ocrText);
    // final String variableSymbol = ocrService.findVariableSymbol(ocrText);
    //
    // final String supplierSection = ocrService.extractSupplierSection(ocrText);
    // final String supplierName = ocrService.extractSupplierName(supplierSection);
    // final String supplierAddress = ocrService.extractSupplierAddress(supplierSection);
    // final String supplierPostalCode = ocrService.extractSupplierPostalCode(supplierSection);
    // final String supplierCity = ocrService.extractSupplierCity(supplierSection);
    // invoiceImport.setPartner_vat_id(vatId);
    // invoiceImport.setPartner_tax_id(dic);
    // invoiceImport.setPartner_registration_number(ico);
    // invoiceImport.setImport_date(new java.util.Date());
    // invoiceImport.setIssue_date(issueDateStr);
    // invoiceImport.setDue_date(dueDateStr);
    // invoiceImport.setTax_date(deliveryDateStr);
    // invoiceImport.setVariable_symbol(variableSymbol);
    // invoiceImport.setPartner_name(supplierName);
    // invoiceImport.setPartner_zip(supplierPostalCode);
    // invoiceImport.setPartner_street(supplierAddress);
    // invoiceImport.setPartner_city(supplierCity);
    // invoiceImport.setStatus("UNPROCESSED");
    // invoiceImport.setPdf_name(pdfName);
    // invoiceImport.setCompany(company);
    //
    // return invoiceImportRepository.save(invoiceImport);

  // @Override
  // public InvoiceImportResponseDto getInvoiceById(Long id) {
  //   return invoiceImportRepository.findById(id)
  //       .map(invoiceImport -> new InvoiceImportResponseDto(
  //           invoiceImport.getId(),
  //           invoiceImport.getImport_date(),
  //           new InvoiceDetailsDTO(
  //               invoiceImport.getInvoice_number(),
  //               invoiceImport.getVariable_symbol(),
  //               invoiceImport.getVariable_symbol(),
  //               invoiceImport.getIssue_date(),
  //               invoiceImport.getTax_date(),
  //               invoiceImport.getDue_date()
  //           ),
  //           new PartnerDto(
  //               invoiceImport.getPartner_name(),
  //               invoiceImport.getPartner_city(),
  //               invoiceImport.getPartner_street(),
  //               invoiceImport.getPartner_zip(),
  //               invoiceImport.getPartner_registration_number(),
  //               invoiceImport.getPartner_tax_id(),
  //               invoiceImport.getPartner_vat_id()
  //           ),
  //           invoiceImport.getStatus(),
  //           invoiceImport.getCompany().getId()
  //       ))
  //       .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + id + " not found"));
  //   return null;
  // }

  @Override
  public PageResponse<InvoiceResponseDTO> findInvoicesByCompanyId(final int page, final int size,
      final Long companyId,
      final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    final Page<Invoice> invoices = invoiceRepository.findAll(withCompanyId(companyId), pageable);

    final List<InvoiceResponseDTO> invoiceResponse = invoices.stream()
        .map(invoiceMapper::toInvoiceTableResponse)
        .toList();
    return new PageResponse<>(
        invoiceResponse,
        invoices.getNumber(),
        invoices.getSize(),
        invoices.getTotalElements(),
        invoices.getTotalPages(),
        invoices.isFirst(),
        invoices.isLast()
    );
  }
    // return invoiceImportRepository.findByCompanyIdAndUserId(companyId, currentUserId)
    //     .stream()
    //     .map(invoiceImport -> new InvoiceImportResponseDto(
    //         invoiceImport.getId(),
    //         invoiceImport.getImport_date(),
    //         new InvoiceResponseDetailsDTO(
    //             invoiceImport.getInvoice_number(),
    //             invoiceImport.getVariable_symbol(),
    //             invoiceImport.getVariable_symbol(),
    //             invoiceImport.getIssue_date(),
    //             invoiceImport.getTax_date(),
    //             invoiceImport.getDue_date()
    //         ),
    //         new PartnerDTO(
    //             invoiceImport.getPartner_name(),
    //             invoiceImport.getPartner_city(),
    //             invoiceImport.getPartner_street(),
    //             invoiceImport.getPartner_zip(),
    //             invoiceImport.getPartner_registration_number(),
    //             invoiceImport.getPartner_tax_id(),
    //             invoiceImport.getPartner_vat_id()
    //         ),
    //         invoiceImport.getStatus(),
    //         invoiceImport.getCompany().getId()
    //     ))
    //     .collect(Collectors.toList());

}
