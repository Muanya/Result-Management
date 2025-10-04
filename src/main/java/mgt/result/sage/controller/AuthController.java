package mgt.result.sage.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mgt.result.sage.dto.*;
import mgt.result.sage.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        AuthToken tokens = authService.register(registerRequest);
        Cookie refreshCookie = authService.getRefreshCookie(tokens);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AuthResponse(tokens.getAccessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest loginRequest, HttpServletResponse response) {
        AuthToken tokens = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        Cookie refreshCookie = authService.getRefreshCookie(tokens);

        response.addCookie(refreshCookie);
        return ResponseEntity.ok(new AuthResponse(tokens.getAccessToken()));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        String email = authService.getEmailFromToken(refreshToken);
        AuthToken newAccessToken = authService.refreshUserToken(email, refreshToken);
        Cookie refreshCookie = authService.getRefreshCookie(newAccessToken);
        response.addCookie(refreshCookie);
        return ResponseEntity.ok(new AuthResponse(newAccessToken.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        String email = authService.getEmailFromToken(token.replace("Bearer ", ""));
        authService.logout(email);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetail> me(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        UserDetail userDetail =  authService.getUserDetailFromToken(token);
        return ResponseEntity.ok(userDetail);
    }
}
