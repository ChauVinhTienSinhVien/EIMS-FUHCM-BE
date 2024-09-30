package com.fullsnacke.eimsfuhcmbe.service.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        // Fetch the user from the repository using their email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }
        User user = userOptional.get();

        // Generate JWT token for the user
        String token = jwtTokenProvider.generateToken(user);

        // Add the token to the response header
        response.setHeader("Authorization", "Bearer " + token);
        String role = user.getRole().getName();

        // Redirect URL
        String redirectUrl = "http://localhost:5173/home";

        // Perform the redirect based on the user's role
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);


//        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
//        OAuth2User oAuth2User = authToken.getPrincipal();
//
//        String email = oAuth2User.getAttribute("email");
//
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (!userOptional.isPresent()) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
//            return;
//        }
//        User user = userOptional.get();
//
//        String token = jwtTokenProvider.generateToken(user);
//        String role = user.getRole().getName();
//
//        // Create a JSON response
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("token", token);
//        responseBody.put("role", role);
//        responseBody.put("email", email);
//
//        // Convert the map to JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonResponse = objectMapper.writeValueAsString(responseBody);
//
//        // Set response headers
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Authorization", "Bearer " + token);
//        response.setHeader("Access-Control-Expose-Headers", "Authorization");
//
//        // Write JSON response
//        response.getWriter().write(jsonResponse);
//        response.getWriter().flush();
//
//        // Redirect
//        String redirectUrl = "http://localhost:5173/home";
//        response.setHeader("Location", redirectUrl);
//        response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    }
}
