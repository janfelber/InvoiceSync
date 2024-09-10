package com.invoicesync.service;

import com.invoicesync.dto.CredentialsDto;
import com.invoicesync.dto.UserCredenialDto;
import com.invoicesync.exceptions.AppException;
import com.invoicesync.mappers.UserCredentialMapper;
import com.invoicesync.module.UserCredential;
import com.invoicesync.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialMapper userCredentialMapper;

    public UserCredenialDto login(CredentialsDto credentialsDto) {
        UserCredential user = userCredentialRepository.findByLogin(credentialsDto.login())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()),
                user.getPassword())) {
            return userCredentialMapper.toUserCredenialDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }
}
