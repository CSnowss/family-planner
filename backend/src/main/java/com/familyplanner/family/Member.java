package com.familyplanner.family;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "members")
public class Member {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String color;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Member() {}

    public Member(UUID familyId, String name, String email, String passwordHash, Role role, String color) {
        this.familyId = familyId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.color = color;
    }

    public UUID getId() { return id; }
    public UUID getFamilyId() { return familyId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public String getColor() { return color; }
}
