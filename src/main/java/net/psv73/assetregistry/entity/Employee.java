package net.psv73.assetregistry.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="employees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="client_id")
  private Client client;
  private String lastName;
  private String firstName;
}