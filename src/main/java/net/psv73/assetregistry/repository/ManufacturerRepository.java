package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> { }
