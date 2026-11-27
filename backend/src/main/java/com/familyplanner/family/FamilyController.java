package com.familyplanner.family;

import com.familyplanner.auth.AuthenticatedMember;
import com.familyplanner.auth.AuthDtos;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FamilyController {

    public record CreateMemberRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull Role role,
            @NotNull @Pattern(regexp = AuthDtos.COLOR_PATTERN) String color) {}

    public record FamilyResponse(java.util.UUID id, String name, List<MemberResponse> members) {}

    private final FamilyRepository families;
    private final MemberRepository members;
    private final PasswordEncoder encoder;

    public FamilyController(FamilyRepository families, MemberRepository members, PasswordEncoder encoder) {
        this.families = families;
        this.members = members;
        this.encoder = encoder;
    }

    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal AuthenticatedMember me) {
        return members.findById(me.memberId()).map(MemberResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown member"));
    }

    @GetMapping("/family")
    public FamilyResponse family(@AuthenticationPrincipal AuthenticatedMember me) {
        Family family = families.findById(me.familyId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        List<MemberResponse> list = members.findByFamilyIdOrderByName(family.getId()).stream()
                .map(MemberResponse::from).toList();
        return new FamilyResponse(family.getId(), family.getName(), list);
    }

    @PostMapping("/family/members")
    @PreAuthorize("hasRole('PARENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(@AuthenticationPrincipal AuthenticatedMember me,
                                    @Valid @RequestBody CreateMemberRequest req) {
        if (members.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        Member member = members.save(new Member(me.familyId(), req.name().trim(), req.email().trim(),
                encoder.encode(req.password()), req.role(), req.color()));
        return MemberResponse.from(member);
    }
}
