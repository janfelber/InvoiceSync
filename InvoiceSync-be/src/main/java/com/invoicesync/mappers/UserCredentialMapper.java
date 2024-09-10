package com.invoicesync.mappers;

import com.invoicesync.dto.UserCredenialDto;
import com.invoicesync.module.UserCredential;
import org.mapstruct.Mapper;

@FunctionalInterface
@Mapper(componentModel = "spring")
public interface UserCredentialMapper {

    UserCredenialDto toUserCredenialDto(UserCredential userCredential);
}
