package com.invoicesync.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredenialDto {

    private Long id;
    private String username;
    private String password;
    private String login;
    private String token;

}
