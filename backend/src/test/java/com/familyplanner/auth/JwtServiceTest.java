package com.familyplanner.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.familyplanner.config.AppProperties;
import com.familyplanner.family.Member;
import com.familyplanner.family.Role;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234";

    private JwtService service(String secret, Duration ttl) {
        return new JwtService(new AppProperties(new AppProperties.Jwt(secret, ttl), java.util.List.of()));
    }

    private Member member() {
        return new Member(UUID.randomUUID(), "Sam", "sam@example.com", "hash", Role.CHILD, "#ff0000");
    }

    @Test
    void roundTripsClaims() {
        JwtService jwt = service(SECRET, Duration.ofMinutes(5));
        Member m = member();

        AuthenticatedMember parsed = jwt.parse(jwt.issue(m)).orElseThrow();

        assertThat(parsed.memberId()).isEqualTo(m.getId());
        assertThat(parsed.familyId()).isEqualTo(m.getFamilyId());
        assertThat(parsed.role()).isEqualTo(Role.CHILD);
    }

    @Test
    void rejectsExpiredTamperedAndForeignTokens() {
        Member m = member();
        assertThat(service(SECRET, Duration.ofSeconds(-5)).parse(service(SECRET, Duration.ofSeconds(-5)).issue(m))).isEmpty();

        JwtService jwt = service(SECRET, Duration.ofMinutes(5));
        assertThat(jwt.parse(jwt.issue(m) + "x")).isEmpty();
        assertThat(jwt.parse("garbage")).isEmpty();
        assertThat(jwt.parse(service("another-secret-another-secret-another-1", Duration.ofMinutes(5)).issue(m))).isEmpty();
    }
}
