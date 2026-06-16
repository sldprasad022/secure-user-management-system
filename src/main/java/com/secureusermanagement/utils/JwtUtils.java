package com.secureusermanagement.utils;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.secureusermanagement.dto.JWTConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils
{
    public String generateToken(Long userId,String email,String role)
    {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(calculateExpirationTime())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String token)
    {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token)
    {
        return ((Number) extractClaims(token)
                .get("userId"))
                .longValue();
    }

    public String extractRole(String token)
    {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token)
    {
        try
        {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        }
        catch (ExpiredJwtException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token is expired");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private Date calculateExpirationTime()
    {
        return new Date(System.currentTimeMillis()+ JWTConstants.JWT_EXPIRATION_TIME);
    }

    private SecretKey getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(JWTConstants.JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}