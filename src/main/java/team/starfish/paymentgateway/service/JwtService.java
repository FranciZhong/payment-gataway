package team.starfish.paymentgateway.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team.starfish.paymentgateway.dto.auth.JwtValidResultDto;
import team.starfish.paymentgateway.dto.auth.WalletDto;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Resource
    private Gson gson;

    @Value("${JWT_EXPIRATION}")
    private Long jwtExpiration;

    private final Key jwtSecretKey;
    private final JwtParser jwtParser;

    private final String CLAIM_WALLET = "wallet";

    public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build();
    }

    public String generateJwtToken(WalletDto wallet) {
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(CLAIM_WALLET, gson.toJson(wallet));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtSecretKey)
                .compact();
    }

    public WalletDto parseFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String walletJson = claims.get(CLAIM_WALLET, String.class);
        return gson.fromJson(walletJson, WalletDto.class);
    }

    public JwtValidResultDto validateJwtToken(String token) {
        try {
            WalletDto wallet = this.parseFromJwtToken(token);
            return new JwtValidResultDto(true, wallet);

        } catch (ExpiredJwtException ex) {
            return new JwtValidResultDto(false, "Token has expired.");

        } catch (Exception ex) {
            return new JwtValidResultDto(false, "Invalid token.");
        }
    }
}