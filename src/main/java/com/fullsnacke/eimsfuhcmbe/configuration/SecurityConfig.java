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

<<<<<<< Updated upstream
    };
=======
    private String[] PUBLIC_ENDPOINTS = {
            "/",
            "/error",
            "/favicon.ico",
            "/**/*.png",
            "/**/*.gif",
            "/**/*.svg",
            "/**/*.jpg",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/auth/**",
//            "/oauth2/**"
    };

>>>>>>> Stashed changes
    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
<<<<<<< Updated upstream
                        .requestMatchers("/api/lecturer/**").hasRole("LECTURER")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
=======
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
>>>>>>> Stashed changes
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
//                        .failureHandler();
                );
        return http.build();
    }
}

