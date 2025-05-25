package com.invoicesync.config;

// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

// private final JwtService jwtService;
//
// private final UserDetailsService userDetailsService;
//
// private final TokenRepository tokenRepository;
//
// @Override
// protected void doFilterInternal(
//         @NonNull HttpServletRequest request,
//         @NonNull HttpServletResponse response,
//         @NonNull FilterChain filterChain
// ) throws ServletException, IOException {
//
//     final String authHeader = request.getHeader("Authorization");
//     final String jwtToken;
//     final String login;
//
//     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//         filterChain.doFilter(request, response);
//         return;
//     }
//
//     jwtToken = authHeader.substring(7);
//     login = jwtService.extractLogin(jwtToken);
//
//     if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//         UserDetails userDetails = this.userDetailsService.loadUserByUsername(login);
//         var isTokenValid = tokenRepository.findByToken(jwtToken)
//             .map(token -> !token.isExpired() && !token.isRevoked())
//             .orElse(false);
//         if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
//             UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                     userDetails,
//                     null,
//                     userDetails.getAuthorities()
//             );
//             authToken.setDetails(
//                     new WebAuthenticationDetailsSource().buildDetails(request)
//             );
//             SecurityContextHolder.getContext().setAuthentication(authToken);
//         }
//     }
//     filterChain.doFilter(request, response);
// }
// }
