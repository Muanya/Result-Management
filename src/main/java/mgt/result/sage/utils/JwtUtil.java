package mgt.result.sage.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mgt.result.sage.config.SecurityConfig;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtUtil(SecurityConfig config) {
        SECRET_KEY = config.getSecret();
        ACCESS_EXPIRATION = Long.parseLong(config.getSecretExpiration());
        REFRESH_EXPIRATION = Long.parseLong(config.getSecretExpiration());
    }

    public String generateToken(String email, Long expiration) {
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).compact();
    }

    public  String generateAccessToken(String email){
        return generateToken(email, ACCESS_EXPIRATION);
    }

    public  String generateRefreshToken(String email){
        return generateToken(email, REFRESH_EXPIRATION);
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, String email) {
        return email.equals(extractEmail(token)) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
