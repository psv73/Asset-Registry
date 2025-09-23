package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="models", uniqueConstraints=@UniqueConstraint(columnNames={"manufacturer_id","name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Model {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="manufacturer_id")
  private Manufacturer manufacturer;
  @Column(nullable=false) private String name;
  private String mtm;
}