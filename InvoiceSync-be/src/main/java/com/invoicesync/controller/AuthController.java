package com.invoicesync.controller;


import com.invoicesync.config.UserAuthProvider;
import com.invoicesync.dto.CredentialsDto;
import com.invoicesync.dto.UserCredenialDto;
import com.invoicesync.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {


    private final UserCredentialService userCredentialService;
    private final UserAuthProvider userAuthProvider;

    @PostMapping("/login")
    public ResponseEntity<UserCredenialDto> login (@RequestBody CredentialsDto credentialsDto) {
        UserCredenialDto user = userCredentialService.login(credentialsDto);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }
}
