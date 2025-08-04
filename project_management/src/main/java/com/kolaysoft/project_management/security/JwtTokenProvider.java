package com.kolaysoft.project_management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // Token geçerlilik süresi: 1 saat
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    // Gizli anahtar – Gerçek uygulamada env dosyasına konulmalı!
    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 256-bit

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ✅ Token üret
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Token geçerli mi?
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Geçersiz token: " + e.getMessage());
            return false;
        }
    }

    // ✅ Token'dan kullanıcı adını al
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // ✅ Token'dan roller alınabilir (isteğe bağlı)
    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", String.class);
    }
}
