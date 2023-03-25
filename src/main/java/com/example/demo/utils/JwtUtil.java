package com.example.demo.utils;

import com.example.demo.authentication.JwtUserDetails;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
  private final String jwtSecret = "secretKey";
  private final int jwtExpiration = 86400000;

  public String generateJwtToken(Authentication authentication) {
    JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException exception) {
      logger.error("Invalid JWT signature: {}", exception.getMessage());
    } catch (MalformedJwtException exception) {
      logger.error("Invalid JWT token: {}", exception.getMessage());
    } catch (ExpiredJwtException exception) {
      logger.error("JWT token is expired: {}", exception.getMessage());
    } catch (UnsupportedJwtException exception) {
      logger.error("JWT token is unsupported: {}", exception.getMessage());
    } catch (IllegalArgumentException exception) {
      logger.error("JWT claims string is empty: {}", exception.getMessage());
    }

    return false;
  }
}
