package com.invoicesync.modules.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.invoicesync.config.BeansConfig;
import com.invoicesync.config.SecurityConfig;
import com.invoicesync.core.Api;
import com.invoicesync.modules.auth.security.CustomAuthenticationEntryPoint;
import com.invoicesync.modules.auth.security.JwtAuthenticationFilter;
import com.invoicesync.modules.auth.security.JwtService;
import com.invoicesync.modules.auth.service.MobileAuthService;

@WebMvcTest(controllers = MobileAuthController.class)
@ContextConfiguration(classes = {
    MobileAuthController.class,
    SecurityConfig.class,
    BeansConfig.class,
    JwtAuthenticationFilter.class,
    CustomAuthenticationEntryPoint.class
})
class MobileAuthControllerSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MobileAuthService mobileAuthService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private JwtService jwtService;

  @Test
  void unauthenticatedGenerateQrRequestIsRejected() throws Exception {
    mockMvc.perform(get(Api.AUTH_MOBILE + Api.AUTH_MOBILE_QR))
        .andExpect(status().isUnauthorized());
  }

}
