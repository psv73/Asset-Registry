package net.psv73.assetregistry.web.response;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetResponseDto {
    private Long id;
    private Long clientId;
    private Long modelId;
    private Long statusId;
    private Long osId;
    private String inventoryCode;
    private String serialNumber;
    private String hostname;
    private String ip;                 // строкой
    private boolean dhcp;
    private LocalDate purchaseDate;
    private Map<String, Object> params;
    private String note;
    private OffsetDateTime createDatetime;
    private OffsetDateTime updateDatetime;
}
