package com.fullsnacke.eimsfuhcmbe.service.authentication;

import com.fullsnacke.eimsfuhcmbe.dto.request.AuthenticationRequest;
import com.fullsnacke.eimsfuhcmbe.dto.request.IntrospectRequest;
import com.fullsnacke.eimsfuhcmbe.dto.response.AuthenticationResponse;
import com.fullsnacke.eimsfuhcmbe.dto.response.IntrospectResponse;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.JwtTokenProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    JwtTokenProvider jwtTokenProvider;
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    //check login voi email va password
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        var user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new AuthenticationProcessException(ErrorCode.LOGIN_FAILED));
//        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
//        if(!authenticated)
//            throw new AuthenticationProcessException(ErrorCode.LOGIN_FAILED);
//
//        var token = jwtTokenProvider.generateToken(user);
//
//        return AuthenticationResponse.builder()
//                .token(token)
//                .authenticated(authenticated)
//                .build();
//    }

    //ktr xem token con han su dung khong
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }
}
