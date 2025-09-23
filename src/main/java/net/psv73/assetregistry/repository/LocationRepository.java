package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LocationRepository extends JpaRepository<Location, Long> { }
