package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmployeeRepository extends JpaRepository<Employee, Long> { }
