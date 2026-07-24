package com.invoicesync.modules.auth.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.invoicesync.core.common.BaseEntity;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

  private final OwnershipLoaderRegistry registry;

  private static final List<Class<? extends Annotation>> KNOWN_ANNOTATIONS = List.of(
      OwnedReceipt.class,
      OwnedInvoice.class,
      OwnedCompany.class,
      OwnedXML.class,
      OwnedReceiptDocument.class,
      OwnedInvoiceDocument.class,
      OwnedPostAccount.class
  );

  @Around("@annotation(com.invoicesync.modules.auth.security.RequiresOwnership)")
  public Object checkOwnership(ProceedingJoinPoint pjp) throws Throwable {
    Method method = ((MethodSignature) pjp.getSignature()).getMethod();
    Annotation[][] paramAnnotations = method.getParameterAnnotations();
    Object[] args = pjp.getArgs();

    final String currentUserUuid = SecurityContextHolder.getContext().getAuthentication().getName();
    boolean foundAny = false;

    for (int i = 0; i < paramAnnotations.length; i++) {
      for (Annotation a : paramAnnotations[i]) {
        if (KNOWN_ANNOTATIONS.contains(a.annotationType())) {
          foundAny = true;
          final Long id = (Long) args[i];
          final BaseEntity entity = registry.get(a.annotationType()).apply(id);

          if (!currentUserUuid.equals(entity.getCreatedBy())) {
            throw new EntityNotFoundException(entity.getClass().getSimpleName() + " with id " + id + " does not exist");
          }
        }
      }
    }

    if (!foundAny) {
      throw new IllegalStateException(
          "@RequiresOwnership is used on method " + method.getName()
              + ", but no parameter has a recognized ownership annotation (check KNOWN_ANNOTATIONS)"
      );
    }

    return pjp.proceed();
  }

}
