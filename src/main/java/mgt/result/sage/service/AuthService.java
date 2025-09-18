package mgt.result.sage.service;

import mgt.result.sage.dto.AuthResponse;
import mgt.result.sage.dto.RegisterRequest;
import mgt.result.sage.entity.Student;
import mgt.result.sage.entity.User;
import mgt.result.sage.repository.UserRepository;
import mgt.result.sage.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email Already exist");
        }

        String accessToken = jwtUtil.generateAccessToken(req.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(req.getEmail());

        Student user = new Student();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setRefreshToken(refreshToken);
        userRepo.save(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshUserToken(String email, String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken, email)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("User refresh token does not match");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        user.setRefreshToken(newRefreshToken);

        userRepo.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken);

    }

    public void logout(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(null);
        userRepo.save(user);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }
}
