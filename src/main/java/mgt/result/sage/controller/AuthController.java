package mgt.result.sage.controller;

import mgt.result.sage.dto.AuthRequest;
import mgt.result.sage.dto.AuthResponse;
import mgt.result.sage.dto.RegisterRequest;
import mgt.result.sage.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String email,@RequestParam String refreshToken) {
        return authService.refreshUserToken(email, refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token.replace("Bearer ", ""));
        authService.logout(email);
        return "Logged out successfully";
    }

    @GetMapping("/me")
    public String me(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        return "You are logged in as: " + authService.getEmailFromToken(token);
    }
}
