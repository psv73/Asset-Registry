package net.psv73.assetregistry.repository;

import net.psv73.assetregistry.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Page<Asset> findAllByInventoryCode(String inventoryCode, Pageable pageable);

}
