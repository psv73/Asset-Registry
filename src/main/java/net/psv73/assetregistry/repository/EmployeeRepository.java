package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  @Query("select e from Employee e where e.client.id = :clientId")
  List<Employee> findAllByClientId(@Param("clientId") Long clientId);
}