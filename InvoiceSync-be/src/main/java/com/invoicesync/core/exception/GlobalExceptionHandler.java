package com.invoicesync.core.exception;

import java.util.Map;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(LimitExceededException.class)
  public ResponseEntity<String> handleLimitExceededException(LimitExceededException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
  }

  @ExceptionHandler(EmailAlreadySubscribedException.class)
  public ResponseEntity<String> handleEmailAlreadySubscribedException(final EmailAlreadySubscribedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(FeatureMissingException.class)
  public ResponseEntity<String> handleFeatureMissingException(final FeatureMissingException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(DownloadDocumentException.class)
  public ResponseEntity<String> handleCannotDeleteDocument(final DownloadDocumentException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(NumberConfigMissingException.class)
  public ResponseEntity<String> handleNumberConfigMissingException(final NumberConfigMissingException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(CompanyRegistrationNumberNotFound.class)
  public ResponseEntity<String> handleCompanyRegistrationNumberNotFound(final CompanyRegistrationNumberNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(UserNameExists.class)
  public ResponseEntity<Map<String, String>> handleUserNameExists(final UserNameExists ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("field", "username", "message", ex.getMessage()));
  }

  @ExceptionHandler(CompanyRegistrationNumberExists.class)
  public ResponseEntity<Map<String, String>> handleCompanyRegistrationNumberExists(
      final CompanyRegistrationNumberExists ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("field", "registrationNumber", "message", ex.getMessage()));
  }

  @ExceptionHandler(RegisterEmailExists.class)
  public ResponseEntity<Map<String, String>> handleRegisterEmailExists(final RegisterEmailExists ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("field", "email", "message", ex.getMessage()));
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<String> handleInvalidRefreshToken(final InvalidRefreshTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not recognized");
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<String> handleInvalidToken(final InvalidTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalid or expired");
  }

  @ExceptionHandler(AccountLockedException.class)
  public ResponseEntity<String> handleAccountLocked(final AccountLockedException ex) {
    return ResponseEntity.status(HttpStatus.LOCKED)
        .body("Account temporarily locked due to too many failed login attempts");
  }

  @ExceptionHandler(CompanyHasLinkedDocumentsException.class)
  public ResponseEntity<String> handleCompanyHasLinkedDocuments(final CompanyHasLinkedDocumentsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(InvoiceExtractionException.class)
  public ResponseEntity<String> handleInvoiceExtractionException(final InvoiceExtractionException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(FileUploadException.class)
  public ResponseEntity<String> handleFileUploadException(final FileUploadException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidFileTypeException.class)
  public ResponseEntity<String> handleInvalidFileType(final InvalidFileTypeException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(NoActiveSubscriptionException.class)
  public ResponseEntity<String> handleNoActiveSubscriptionException(final NoActiveSubscriptionException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

}
