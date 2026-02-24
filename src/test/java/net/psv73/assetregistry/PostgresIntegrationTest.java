package net.psv73.assetregistry;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class PostgresIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/asset_registry");
        registry.add("spring.datasource.username", () -> "asset");
        registry.add("spring.datasource.password", () -> "asset");
    }
}