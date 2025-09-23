package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AssetRepository extends JpaRepository<Asset, Long> { }
