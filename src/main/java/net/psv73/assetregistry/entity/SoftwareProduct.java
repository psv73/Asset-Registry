package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="software_products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SoftwareProduct {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String name;
  private String vendor;
}