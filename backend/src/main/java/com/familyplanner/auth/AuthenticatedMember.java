package com.familyplanner.auth;

import com.familyplanner.family.Role;
import java.util.UUID;

public record AuthenticatedMember(UUID memberId, UUID familyId, Role role) {}
