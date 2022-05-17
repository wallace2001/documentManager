package com.rest.documentManager.config.jwt;

import com.rest.documentManager.services.exceptions.ErrorLoginException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.java.Log;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Log
public class JwtProvider {

    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    private static String jwtSecret = "javamaster";

    public static String generateToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.severe("invalid token");
        }
        return false;
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
