package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="asset_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetMovement {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="asset_id") private Asset asset;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private Employee employee;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="office_id") private Office office;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="status_id") private Status status;
  @Column(nullable=false) private java.time.LocalDate effectiveFrom;
  @Column(nullable=false) private java.time.OffsetDateTime recordedAt;
  @Column(columnDefinition="text") private String note;
  @PrePersist void prePersist(){ if(recordedAt==null) recordedAt=java.time.OffsetDateTime.now(); }
}