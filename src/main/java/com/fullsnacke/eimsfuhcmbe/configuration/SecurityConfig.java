package com.fullsnacke.eimsfuhcmbe.configuration;

import com.fullsnacke.eimsfuhcmbe.service.AuthenticationServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.authenticationresult.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private String[] PUBLIC_ENDPOINT = {

    };
    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        // Thêm Cross-Origin-Opener-Policy để cho phép giao tiếp
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                            response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                            response.setHeader("Cross-Origin-Resource-Policy", "same-site");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/lecturer/**").hasRole("LECTURER")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(authenticationServiceImpl))
                        .successHandler(successHandler)
                );
        return http.build();
    }
}

