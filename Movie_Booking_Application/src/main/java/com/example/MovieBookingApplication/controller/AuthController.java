package com.example.MovieBookingApplication.controller;

import com.example.MovieBookingApplication.DTO.*;
import com.example.MovieBookingApplication.entity.RefreshToken;
import com.example.MovieBookingApplication.entity.User;
import com.example.MovieBookingApplication.jwt.JwtService;
import com.example.MovieBookingApplication.service.AuthenticationService;
import com.example.MovieBookingApplication.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registernormaluser")
    public ResponseEntity<User> registerNormalUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(authenticationService.registerNormalUser(registerRequestDTO));
    }

    @PostMapping("/login")
    public  ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Optional<RefreshToken> tokenOpt = refreshTokenService.findByToken(requestRefreshToken);
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Refresh token is not in database!");
        }
        RefreshToken refreshToken = tokenOpt.get();
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, requestRefreshToken));
    }

}
