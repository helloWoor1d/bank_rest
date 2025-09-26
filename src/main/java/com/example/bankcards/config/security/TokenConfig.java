package com.example.bankcards.config.security;

import com.example.bankcards.security.model.SecurityUser;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class TokenConfig {
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                var token = context.getPrincipal();
                Object principal = token.getPrincipal();

                if (principal instanceof SecurityUser securityUser) {
                    context.getClaims().claims((claims) -> {
                        claims.put("sub", securityUser.getId());
                        Set<String> roles = securityUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                        claims.put("role", roles);
                    });
                }
            }
        });
    }
}
