package gate.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {


    private static String secret = "4f189735951b13373e3a289dfdd51dd76d430b87d57695bc0a6b2efb5e5cd2522b05e45bdaa04c2a16d9d1e7afaf3ba0d395e33920fb6655be319c2ce08aebab4238e8993cc5823777b88cf90f8895a7b48647e85ce719f7b21e9d4c86d810644a8fda9f3b476792bab8c809219dbd7675d6691667c74ea9edc166c5ee5d63f89dccb74f6d144380d810c0d43145cd14a0bc33ec98b83641aff44d70a9642ca3a8310684c4fa4ea5b334f3b814defde72e5fb1e703cf869aecbb2e5c687fd7c149049bcb2ccf34d7864aa509ce9deb2b6af41411d29acfda7aa205c0abedec4e166b566e28b7915820021c5b2177a4bc336f110b12ad9a2f289a9b05a80ada09";

    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    public static String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SECRET_KEY)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        System.out.println("Generated Token: " + token); // Log the generated token
        return token;
    }

    public static boolean validateToken(String token) {
        try {
//            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            System.out.println("Token validated successfully: " + token); // Log successful validation
            return true;
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }


    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody();
        System.out.println("Token Claims: " + claims); // Log token claims
        return claims.getSubject();
    }

}
