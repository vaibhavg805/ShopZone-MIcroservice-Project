package com.vaibhav.product_service.security;

import com.vaibhav.product_service.config.ExternalConfigSecret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000*60*15;
    private ExternalConfigSecret jwtConfig;

    private Key generateSecretkey(){
        byte[] arr = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(arr);
    }

    public JwtService(ExternalConfigSecret jwtConfig) {
        this.SECRET_KEY = jwtConfig.getSecret();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        return !extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(generateSecretkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
