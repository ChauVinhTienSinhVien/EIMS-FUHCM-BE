package com.fullsnacke.eimsfuhcmbe.configuration;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTUtils {

    private long TOKEN_VALIDITY;
    private long TOKEN_VALIDITY_REMEMBER;
    private final Key key;

    public JWTUtils(@Value("${jwt.secretKey}") String secret, @Value("${jwt.expirationInMs}") long expirationInMs, @Value("${jwt.expirationInMsRemember}") long expirationInMsRemember) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.TOKEN_VALIDITY = expirationInMs;
        this.TOKEN_VALIDITY_REMEMBER = expirationInMsRemember;
    }

    public String createToken(User user, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity = rememberMe ? new Date(now + TOKEN_VALIDITY_REMEMBER) : new Date(now + TOKEN_VALIDITY);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        } catch (JwtException | IllegalArgumentException ignored) {
            return null;
        }
    }
}
