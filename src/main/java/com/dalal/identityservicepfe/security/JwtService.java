package com.dalal.identityservicepfe.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtService {

    @Value("${private-key}")
    private String privateKeyString;

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

        // Reverse the Base64 encoding to access the raw bytes
        byte [] decodedKey = Base64.getUrlDecoder().decode(privateKeyString);

        //Structure the bytes according to the standard PKCS#8 private key protocol specification
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        //Initialize the RSA Key Reconstruction Engine (KeyFactory)
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");


        return keyFactory.generatePrivate(keySpec);
    }

}
