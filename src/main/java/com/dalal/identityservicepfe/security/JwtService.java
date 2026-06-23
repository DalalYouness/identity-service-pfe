package com.dalal.identityservicepfe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${private-key}")
    private String privateKeyString;

    @Value("${public-key}")
    private String publicKeyString;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    public String generateToken(String subject) throws Exception {
        Long expiration = Long.parseLong(jwtExpiration);
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(subject)
                .signWith(getPrivateKeyFromString(), SignatureAlgorithm.RS256)
                .compact();
    }

    private PrivateKey getPrivateKeyFromString() throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String userEmail) throws Exception {
        final String username = extractUsername(token);
        return (username.equals(userEmail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <R> R extractClaim(String token, Function<Claims, R> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKeyFromString()) // Utilisation de la clé publique pour décoder
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PublicKey getPublicKeyFromString() throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}