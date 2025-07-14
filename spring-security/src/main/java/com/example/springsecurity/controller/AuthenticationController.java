package com.example.springsecurity.controller;


import com.example.springsecurity.dto.AuthenticationRequest;
import com.example.springsecurity.dto.AuthenticationResponse;
import com.example.springsecurity.dto.RegisterRequest;
import com.example.springsecurity.dto.UserInfo;
import com.example.springsecurity.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(request));
    }


    @GetMapping("/userinfo")
    public UserInfo getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        UserInfo userInfo = new UserInfo();
        if (principal != null) {
            userInfo.setName(principal.getAttribute("name"));
            userInfo.setEmail(principal.getAttribute("email"));
        } else {
            userInfo.setName(null);
            userInfo.setEmail(null);
        }
        return userInfo;
    }
    //logout ở client side, không cần thiết phải có endpoint logout ở server side
}
