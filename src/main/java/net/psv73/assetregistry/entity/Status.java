package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="statuses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Status {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true, length=64) private String code;
  @Column(nullable=false) private String label;
}