package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;

@Entity @Table(name="assets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asset {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="asset_type_id") private AssetType assetType;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="manufacturer_id") private Manufacturer manufacturer;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="model_id") private Model model;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="device_type_id") private DeviceType deviceType;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="os_id") private Os os;
  @Column(unique=true) private String inventoryCode;
  @Column(unique=true) private String serialNumber;
  private LocalDate deliveryDate;
  private String ipAddress;
  @Column(columnDefinition="text") private String params;
  @Column(columnDefinition="text") private String note;
  @Column(nullable=false) private OffsetDateTime createdAt;
  @Column(nullable=false) private OffsetDateTime updatedAt;
  @PrePersist void prePersist(){ var now=OffsetDateTime.now(); createdAt=now; updatedAt=now; }
  @PreUpdate  void preUpdate(){ updatedAt=OffsetDateTime.now(); }
}