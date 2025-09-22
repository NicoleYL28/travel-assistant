package oocl.travelassistant.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityMillis;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expire-ms:3600000}") long validityMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityMillis = validityMillis;
    }

    public String generateToken(Long userId, String username, String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }
}
