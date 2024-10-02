package com.fullsnacke.eimsfuhcmbe.service.authentication;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements CustomOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            log.info("email is null");
            throw new AuthenticationProcessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("email not found");
                    return new AuthenticationProcessException(ErrorCode.EMAIL_NOT_FOUND);
                });
        log.info("user found");
        return oAuth2User;
    }
}
