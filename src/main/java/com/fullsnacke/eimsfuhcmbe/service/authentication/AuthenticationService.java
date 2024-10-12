package com.fullsnacke.eimsfuhcmbe.service.authentication;

import com.fullsnacke.eimsfuhcmbe.exception.EntityNotFoundException;
import com.fullsnacke.eimsfuhcmbe.util.JWTUtils;
import com.fullsnacke.eimsfuhcmbe.dto.request.IdTokenRequestDto;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final GoogleIdTokenVerifier verifier;

    public AuthenticationService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId, UserRepository userRepository,
                          JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public String loginOAuthGoogle(IdTokenRequestDto requestBody) {
        User user = verifyIDToken(requestBody.getIdToken());

        if (user == null) {
            throw new IllegalArgumentException();
        }

        User isIntheDB = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (isIntheDB == null) {
            throw new EntityNotFoundException(User.class, "email", user.getEmail());
        }

        return jwtUtils.createToken(isIntheDB, false);
    }

    private User verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }

            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String email = payload.getEmail();

            return User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }
}
