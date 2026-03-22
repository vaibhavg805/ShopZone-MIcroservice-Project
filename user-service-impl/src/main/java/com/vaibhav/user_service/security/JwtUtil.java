package com.vaibhav.user_service.security;

import com.vaibhav.user_service.externalconfig.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000*60*15;
    private JwtConfig jwtConfig;

    public JwtUtil(JwtConfig jwtConfig) {
        this.SECRET_KEY = jwtConfig.getSecret();
    }

    private Key generateSecretkey(){
        byte[] arr = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(arr);
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> rolesClaims = new HashMap<>();

        // extract roles from UserDetails
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        rolesClaims.put("roles", roles);
        System.out.println("Roles"+roles);

        return Jwts.builder()
                .setClaims(rolesClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(JWT_EXPIRATION/1000)))
                .signWith(generateSecretkey())
                .compact();
    }

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }

    private <R> R extractClaims(String token, Function<Claims,R> claimsResolver) {
        final Claims getClaim = extractAllClaims(token);
        return claimsResolver.apply(getClaim);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(generateSecretkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValidate(String username, String token){
       boolean usernameResult = extractUsername(token).equals(username);
       boolean timeValidate =  checkTokenExpiry(token);

       return usernameResult && timeValidate;
    }

    public boolean checkTokenExpiry(String token){
      Instant tokenTime = extractClaims(token,Claims::getExpiration).toInstant();
      Instant currentTime = Instant.now();
        return tokenTime.isAfter(currentTime);
    }


}
