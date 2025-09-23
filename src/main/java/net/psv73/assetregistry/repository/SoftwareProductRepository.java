package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.SoftwareProduct;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SoftwareProductRepository extends JpaRepository<SoftwareProduct, Long> { }
