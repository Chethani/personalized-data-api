package com.chethani.personalization;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.chethani.personalization.config.SecurityConfig;

public class JwtAuthenticationConverterTest {

    // entry point isn't used by jwtAuthenticationConverter(), safe to pass null here
    private final SecurityConfig securityConfig = new SecurityConfig(null);
    private final Converter<Jwt, Collection<GrantedAuthority>>converter = securityConfig.realmRoleAuthoritiesConverter();

    @Test
    void shouldReturnNoAuthoritiesWhenRealmAccessClaimMissing() {
        Jwt jwt = buildJwt(Map.of()); // no realm_access claim at all
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldReturnNoAuthoritiesWhenRolesListEmpty() {
        Jwt jwt = buildJwt(Map.of("realm_access", Map.of("roles", List.of())));
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldMapRealmRolesToAuthorities() {
        Jwt jwt = buildJwt(Map.of("realm_access", Map.of("roles", List.of("read:shopper-data"))));
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("read:shopper-data");
    }

    private Jwt buildJwt(Map<String, Object> extraClaims) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("test-subject")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60));
        extraClaims.forEach(builder::claim);
        return builder.build();
    }

}
