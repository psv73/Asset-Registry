package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="asset_software")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetSoftware {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="asset_id")
  private Asset asset;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="product_id")
  private SoftwareProduct product;
  private String version;
  @Column(columnDefinition="text") private String licenseKey; // хранить зашифрованно/маской
  private java.time.LocalDate licenseExpiresOn;
  @Column(columnDefinition="text") private String note;
}