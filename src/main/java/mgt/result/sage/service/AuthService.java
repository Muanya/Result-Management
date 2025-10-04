package mgt.result.sage.service;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mgt.result.sage.dto.AuthToken;
import mgt.result.sage.dto.RegisterRequest;
import mgt.result.sage.dto.Role;
import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Magister;
import mgt.result.sage.entity.Student;
import mgt.result.sage.entity.User;
import mgt.result.sage.repository.UserRepository;
import mgt.result.sage.utils.JwtUtil;
import mgt.result.sage.utils.Util;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Util util;


    public AuthToken register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email Already exist");
        }

        String accessToken = jwtUtil.generateAccessToken(req.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(req.getEmail());


        String role = req.getRole().toLowerCase();

        User user;

        switch (role) {
            case "teacher":
                user = new Magister();
                user.setRole(Role.MAGISTER);
                break;
            case "student":
                user = new Student();
                user.setRole(Role.STUDENT);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + req.getRole());
        }

        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setRefreshToken(refreshToken);
        userRepo.save(user);
        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        user.setRefreshToken(refreshToken);
        userRepo.save(user);
        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken refreshUserToken(String email, String refreshToken) {
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

        return new AuthToken(newAccessToken, newRefreshToken);

    }

    public void logout(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(null);
        userRepo.save(user);
    }

    public UserDetail getUserDetailFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return util.getUserDetailFromUser(user);
    }


    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }

    public Cookie getRefreshCookie(AuthToken tokens) {
        Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("v1/auth/refresh"); // cookie only sent to v1/auth/refresh
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        refreshCookie.setAttribute("SameSite", "None");
        return refreshCookie;
    }
}
