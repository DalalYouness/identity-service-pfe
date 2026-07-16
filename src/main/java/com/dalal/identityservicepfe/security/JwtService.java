package com.dalal.identityservicepfe.security;

import com.dalal.identityservicepfe.entities.Role;
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
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${private-key}")
    private String privateKeyString;

    @Value("${public-key}")
    private String publicKeyString;

    @Value("${jwt.expiration}")
    private String jwtExpiration;


    public String generateToken(String subject, Set<Role> roles, Long id) throws Exception {
        Long expiration = Long.parseLong(jwtExpiration);
        Set<String> rolesNames = roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(subject)
                .claim("roles", rolesNames)
                .claim("id",id)
                .signWith(getPrivateKeyFromString(), SignatureAlgorithm.RS256)
                .compact();
    }

    private PrivateKey getPrivateKeyFromString() throws Exception {

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyString.trim());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    // we dont have to use it
    public boolean isTokenValid(String token, String userEmail) throws Exception {
        final String username = extractUsername(token);
        return (username.equals(userEmail) && !isTokenExpired(token));
    }
    // the same thing as the methode above
    private boolean isTokenExpired(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <R> R extractClaim(String token, Function<Claims, R> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKeyFromString())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PublicKey getPublicKeyFromString() throws Exception {

        byte[] decodedKey = Base64.getDecoder().decode(publicKeyString.trim());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}