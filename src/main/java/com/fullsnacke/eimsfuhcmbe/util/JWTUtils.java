package com.fullsnacke.eimsfuhcmbe.util;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

    private final long TOKEN_VALIDITY;
    private final long TOKEN_VALIDITY_REMEMBER;
    private final Key key;

    UserRepository userRepository;

    public JWTUtils(@Value("${jwt.secretKey}") String secret, @Value("${jwt.expirationInMs}") long expirationInMs, @Value("${jwt.expirationInMsRemember}") long expirationInMsRemember, UserRepository userRepository) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.TOKEN_VALIDITY = expirationInMs;
        this.TOKEN_VALIDITY_REMEMBER = expirationInMsRemember;
        this.userRepository = userRepository;
    }

    public String createToken(User user, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity = rememberMe ? new Date(now + TOKEN_VALIDITY_REMEMBER) : new Date(now + TOKEN_VALIDITY);
        Map<String, Object> claims = new HashMap<>();
        Role role = user.getRole();

        com.fullsnacke.eimsfuhcmbe.enums.Role roleEnum = com.fullsnacke.eimsfuhcmbe.enums.Role.valueOf(role.getName().toUpperCase());

        claims.put("role", roleEnum);


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
            //List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));
            User user = userRepository.findByEmailAAndIsDeleted(claims.getSubject(), false);
            
            if(user == null) {
                return null;
            }

            List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();

            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        } catch (JwtException | IllegalArgumentException ignored) {
            return null;
        }
    }
}
