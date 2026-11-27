package com.familyplanner.auth;

import com.familyplanner.auth.AuthDtos.AuthResponse;
import com.familyplanner.auth.AuthDtos.LoginRequest;
import com.familyplanner.auth.AuthDtos.RegisterFamilyRequest;
import com.familyplanner.family.ApiException;
import com.familyplanner.family.Family;
import com.familyplanner.family.FamilyRepository;
import com.familyplanner.family.Member;
import com.familyplanner.family.MemberRepository;
import com.familyplanner.family.MemberResponse;
import com.familyplanner.family.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String DEFAULT_PARENT_COLOR = "#3b82f6";

    private final FamilyRepository families;
    private final MemberRepository members;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(FamilyRepository families, MemberRepository members, PasswordEncoder encoder, JwtService jwt) {
        this.families = families;
        this.members = members;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse registerFamily(RegisterFamilyRequest req) {
        if (members.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        Family family = families.save(new Family(req.familyName().trim()));
        Member parent = members.save(new Member(
                family.getId(), req.name().trim(), req.email().trim(), encoder.encode(req.password()),
                Role.PARENT, req.color() != null ? req.color() : DEFAULT_PARENT_COLOR));
        return new AuthResponse(jwt.issue(parent), MemberResponse.from(parent));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Member member = members.findByEmailIgnoreCase(req.email().trim())
                .filter(m -> encoder.matches(req.password(), m.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new AuthResponse(jwt.issue(member), MemberResponse.from(member));
    }
}
