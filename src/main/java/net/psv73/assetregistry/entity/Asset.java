package net.psv73.assetregistry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "assets",
        indexes = {
                @Index(name = "idx_assets_model",  columnList = "model_id"),
                @Index(name = "idx_assets_status", columnList = "status_id"),
                @Index(name = "idx_assets_os",     columnList = "os_id"),
                @Index(name = "idx_assets_client", columnList = "client_id"),
                @Index(name = "idx_assets_serial", columnList = "serial_number")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_assets_inventory_code", columnNames = "inventory_code"),
                @UniqueConstraint(name = "uq_assets_serial_number",  columnNames = "serial_number")
        })
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- FK ----
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "os_id", nullable = false)
    private Os os;

    // ---- business fields ----
    @Column(name = "inventory_code", length = 128, unique = true)
    private String inventoryCode;

    @Column(name = "serial_number", length = 128, unique = true)
    private String serialNumber;

    @Column(length = 255)
    private String hostname;

    // postgres INET; маппим как String (например "10.1.2.3")
    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip", columnDefinition = "inet")
    private InetAddress ip;

    @Builder.Default
    @Column(nullable = false)
    private boolean dhcp = true;

    private LocalDate purchaseDate;

    // postgres JSONB; удобно маппить как Map<String,Object> (Hibernate 6)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> params;

    @Column(columnDefinition = "text")
    private String note;

    // ---- audit ----
    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    @UpdateTimestamp
    @Column(name = "update_datetime")
    private OffsetDateTime updateDatetime;
}
