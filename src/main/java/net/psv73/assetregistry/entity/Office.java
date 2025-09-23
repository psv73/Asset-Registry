package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="offices", uniqueConstraints=@UniqueConstraint(columnNames={"location_id","label"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Office {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="location_id")
  private Location location;
  @Column(nullable=false) private String label;
}