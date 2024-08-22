package com.project.shopapp.components;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private int expiration;  // save to an environment variable
    @Value("${jwt.secret}")
    private String SECRET_KEY;  // save to an environment variable

    public String generateToken(User user) throws Exception {
        // properties => claims
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
             throw new InvalidParamException(STR."Cannot create jwt token, error: \{e.getMessage()}");
        }
    }

    // chuyển SECRET_KEY => Key object
    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
//        Decoders.BASE64.decode("vWeVF/cjeaudctyzLhH4cZA8FE6UKytseda5QHoT5No=")
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];  // 256-bit key
        random.nextBytes(bytes);
        String secretKey = Encoders.BASE64.encode(bytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //check if the token has expired
    private boolean isTokenExpired(String token) {
        Date expiration = this.extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
