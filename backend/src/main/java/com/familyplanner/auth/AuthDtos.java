package com.familyplanner.auth;

import com.familyplanner.family.MemberResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    public static final String COLOR_PATTERN = "^#[0-9a-fA-F]{6}$";

    public record RegisterFamilyRequest(
            @NotBlank @Size(max = 100) String familyName,
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @Pattern(regexp = COLOR_PATTERN) String color) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public record AuthResponse(String token, MemberResponse member) {}
}
