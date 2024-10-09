package com.ardur.dom.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class JwtTokenProvider {
//
//    // Use the secret key defined in JwtConstant
////    private final String key = JwtConstant.SECRET_KEY; 
////    private final long EXPIRATION_TIME = 86400000; // 1 day
//
//    private SecretKey key=Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
//
//    public String generateToken(Authentication auth) {
//    	String jwt=Jwts.builder()
//    	.setIssuedAt(new Date())
//    	.setExpiration(new Date(new Date().getTime()+846000000))
//    	.claim("email", auth.getName())
//    	.signWith(key).compact();
//        return jwt;
//    }
//    public String getEmailFromToken(String jwt) {
//    	jwt=jwt.substring(7);
//    
//    Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
//    String email=String.valueOf(claims.get("email"));
//    return email;
//}
//
////    public Claims validateToken(String token) {
////        try {
////            return Jwts.parserBuilder()
////                    .setSigningKey(getSigningKey())
////                    .build()
////                    .parseClaimsJws(token)
////                    .getBody();
////        } catch (Exception e) {
////            throw new BadCredentialsException("Token is invalid", e);
////        }
////    }
//}
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    private final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(Authentication auth) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("email", auth.getName())
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return String.valueOf(claims.get("email"));
        } catch (Exception e) {
            // Handle exceptions as necessary (e.g., log them)
            return null; // or rethrow the exception, or return a specific error
        }
    }
}
