package com.invoicesync.auth;

// @RestController
// @RequestMapping("api/v1/auth")
// @RequiredArgsConstructor
public class AuthenticationController {

    // private final AuthenticationService service;
    //
    // @PostMapping("/register")
    // public ResponseEntity<?> register(
    //         @RequestBody final RegisterRequest request
    // ) {
    //     final var response = service.register(request);
    //     if (request.isMfaEnabled()) {
    //         return ResponseEntity.ok(response);
    //     }
    //     return ResponseEntity.accepted().build();
    // }
    //
    // @PostMapping("/authenticate")
    // public ResponseEntity<AuthenticationResponse> authenticate(
    //         @RequestBody final AuthenticationRequest request
    // ) {
    //     return ResponseEntity.ok(service.authenticate(request));
    // }
    //
    // @PostMapping("/refresh-token")
    // public void refreshToken(
    //         final HttpServletRequest request,
    //         final HttpServletResponse response
    // ) throws IOException {
    //     service.refreshToken(request, response);
    // }
    //
    // @PostMapping("/verify")
    // public ResponseEntity<?> verifyCode(
    //         @RequestBody final VerificationRequest verificationRequest
    // ) {
    //     return ResponseEntity.ok(service.verifyCode(verificationRequest));
    // }
}
