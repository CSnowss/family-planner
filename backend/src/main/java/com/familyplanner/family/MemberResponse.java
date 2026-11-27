package com.familyplanner.family;

import java.util.UUID;

public record MemberResponse(UUID id, UUID familyId, String name, String email, Role role, String color) {

    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getFamilyId(), m.getName(), m.getEmail(), m.getRole(), m.getColor());
    }
}
