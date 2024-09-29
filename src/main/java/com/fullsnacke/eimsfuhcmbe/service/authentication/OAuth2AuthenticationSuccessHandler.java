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


//        // Set response content type and status
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        // Create a simple JSON response containing only the token
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("email", email);
//        responseBody.put("token", token);
//        responseBody.put("role", role);
//
//        // Write the token in the response body
//        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
//        response.flushBuffer();

//        hoặc là responsebody hoặc là getRedirect :<
        // Determine redirect URL based on the user's role
        String redirectUrl = "http://localhost:5173/" + role.toLowerCase() + "/dashboard";

        // Perform the redirect based on the user's role
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
