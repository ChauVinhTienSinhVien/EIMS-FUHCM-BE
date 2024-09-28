package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.User;
<<<<<<< Updated upstream
import com.fullsnacke.eimsfuhcmbe.exception.OAuth2AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
=======
import com.fullsnacke.eimsfuhcmbe.exception.OAuth2AuthenticationProcessingException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
>>>>>>> Stashed changes
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl extends DefaultOAuth2UserService implements AuthenticationService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //check email exist
        String email = oAuth2User.getAttribute("email");
        if(email == null){
<<<<<<< Updated upstream
            throw new OAuth2AuthenticationProcessException("Email not found from Google.", HttpStatus.UNAUTHORIZED.value());
        }

        boolean emailVerified = Boolean.TRUE.equals(oAuth2User.getAttribute("email_verified"));
        if(!emailVerified){
            throw new OAuth2AuthenticationProcessException("Email does not exist", HttpStatus.UNAUTHORIZED.value());
=======
            throw new OAuth2AuthenticationProcessingException("Email not found.");
>>>>>>> Stashed changes
        }
        //------------------------
        //verify email
        boolean email_verified = Boolean.TRUE.equals(oAuth2User.getAttribute("email_verified"));
        if(!email_verified){
            throw new OAuth2AuthenticationProcessingException("Invalid email!");
        }
        //---------------------
        //check email exist in db
        User user = userRepository.findByEmail(email)
<<<<<<< Updated upstream
                .orElseThrow(() -> new OAuth2AuthenticationProcessException("Your email is not permitted to log in to the system.", HttpStatus.FORBIDDEN.value()));
=======
                .orElseThrow(() -> new OAuth2AuthenticationProcessingException("Your account is not permitted to log in to the system."));
        System.out.println("Authenticated user: " + user.getEmail());
>>>>>>> Stashed changes
        return oAuth2User;
    }
}
