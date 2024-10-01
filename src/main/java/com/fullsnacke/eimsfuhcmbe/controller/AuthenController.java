package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.IdTokenRequestDto;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        String authToken = authenticationService.loginOAuthGoogle(requestBody);
        System.out.println(requestBody.getIdToken());
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/user/info")
    public ResponseEntity getUserInfo(Principal principal) {

        System.out.println(principal.toString());
        System.out.println("principle Name: " + principal.getName());
        User user = userServiceImpl.getUserByEmail(principal.getName());
        return ResponseEntity.ok().body(user);
    }

}
