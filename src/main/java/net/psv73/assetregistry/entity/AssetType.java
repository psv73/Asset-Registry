package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="asset_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetType {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String name;
}