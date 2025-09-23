package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StatusRepository extends JpaRepository<Status, Long> { }
