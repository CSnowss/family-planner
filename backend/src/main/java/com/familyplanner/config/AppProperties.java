package com.familyplanner.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, List<String> corsAllowedOrigins) {

    public record Jwt(String secret, Duration expiration) {}
}
