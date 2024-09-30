package com.fullsnacke.eimsfuhcmbe.configuration;

import com.fullsnacke.eimsfuhcmbe.service.authentication.CustomOAuth2UserServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.authentication.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    private String[] PUBLIC_ENDPOINT = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",

    };
    @Autowired
    private CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                        .requestMatchers("/api/lecturers/**").hasRole("LECTURER")
                        .requestMatchers("/api/staffs/**").hasRole("STAFF")
                        .requestMatchers("/api/managers/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(successHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserServiceImpl))
                        .successHandler(successHandler)
                        .failureUrl("http://localhost:5173")
                );
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");

        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

