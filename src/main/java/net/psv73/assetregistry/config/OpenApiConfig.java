package net.psv73.assetregistry.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/** OpenAPI metadata for Swagger UI (dev only) */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Asset Registry API",
                version = "v1",
                description = "Minimal CRUD for assets (development profile)"
        )
)
public class OpenApiConfig {}
