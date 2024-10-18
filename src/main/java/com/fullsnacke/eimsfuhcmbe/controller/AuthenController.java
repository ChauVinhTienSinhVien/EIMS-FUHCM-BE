package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.AuthenticationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.ChangePasswordRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.IdTokenRequestDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.AuthenticationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/oauth")
public class AuthenController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/google/login")
    public ResponseEntity<AuthenticationResponseDTO> LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        AuthenticationResponseDTO authResponse = authenticationService.loginOAuthGoogle(requestBody);
        String authToken = authResponse.getToken();
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO requestBody, HttpServletResponse response) {
        AuthenticationResponseDTO authResponse = authenticationService.loginUserNamePassWord(requestBody);
        String authToken = authResponse.getToken();

        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/add-password")
    public ResponseEntity<?> addPassword(@RequestBody AuthenticationRequestDTO requestBody){
        authenticationService.addPassword(requestBody);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO requestBody) {
        authenticationService.changePassword(requestBody);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .sameSite("Strict")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        User user = userServiceImpl.getUserByEmail(principal.getName());
        System.out.println(user.getFuId());
        return ResponseEntity.ok().body(user);
    }

}
