package com.invoicesync.service;

import com.invoicesync.dto.CredentialsDto;
import com.invoicesync.dto.UserCredenialDto;

public interface UserCredentialService {

    UserCredenialDto login(CredentialsDto credentialsDto);
}
