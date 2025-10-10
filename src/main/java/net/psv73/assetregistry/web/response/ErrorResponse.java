package net.psv73.assetregistry.web.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;                 // HTTP status code
    private String error;               // short code: bad_request / not_found / validation_failed / internal_error
    private String message;             // human message
    private String path;                // request path
    private Map<String, String> fields; // optional: field -> error (для валидации)
}
