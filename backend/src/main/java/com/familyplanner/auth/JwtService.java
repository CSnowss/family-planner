package com.familyplanner.auth;

import com.familyplanner.config.AppProperties;
import com.familyplanner.family.Member;
import com.familyplanner.family.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;
    private final AppProperties props;

    public JwtService(AppProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.jwt().secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issue(Member member) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(member.getId().toString())
                .claim("fid", member.getFamilyId().toString())
                .claim("role", member.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.jwt().expiration())))
                .signWith(key)
                .compact();
    }

    public Optional<AuthenticatedMember> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(new AuthenticatedMember(
                    UUID.fromString(claims.getSubject()),
                    UUID.fromString(claims.get("fid", String.class)),
                    Role.valueOf(claims.get("role", String.class))));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
