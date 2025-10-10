package net.psv73.assetregistry.web.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

public record AssetRequestDto(
        @NotNull Long clientId,
        @NotNull Long modelId,
        @NotNull Long statusId,
        @NotNull Long osId,
        @Size(max = 128) String inventoryCode,
        @Size(max = 128) String serialNumber,
        @Size(max = 255) String hostname,
        String ip,
        boolean dhcp,
        LocalDate purchaseDate,
        Map<String, Object> params,
        String note
) {}
