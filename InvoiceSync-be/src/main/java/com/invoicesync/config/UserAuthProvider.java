package com.invoicesync.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.invoicesync.dto.UserCredenialDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {

    @Value("{security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserCredenialDto dto){
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3_600_000);

        return JWT.create()
                .withSubject(dto.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("id", dto.getId())
                .withClaim("username", dto.getUsername())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decoded = verifier.verify(token);

        UserCredenialDto user = UserCredenialDto.builder()
                .login(decoded.getIssuer())
                .username(decoded.getClaim("username").asString())
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }


}
