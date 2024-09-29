package com.fullsnacke.eimsfuhcmbe.service.authentication;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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

        User user = userRepository.findByEmail(email).get();

        String role = user.getRole().getName();
        String token = jwtTokenProvider.generateToken(user);
        System.out.println(token);

//        Map<String, String> responeBody = new HashMap<>();
//        responeBody.put("email", email);
//        responeBody.put("token", token);
//        responeBody.put("role", role);
//
//        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(responeBody, HttpStatus.OK);
//
//        System.out.println("Home ne: OAuth2AuthenticationSuccessHandler");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
//        response.flushBuffer();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String redirectUrl = "http://localhost:5173/" + role.toLowerCase() + "/dashboard";
        response.addHeader("Authorization", "Bearer " + token);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
