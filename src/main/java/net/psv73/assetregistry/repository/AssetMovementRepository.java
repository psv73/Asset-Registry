package net.psv73.assetregistry.repository;

import net.psv73.assetregistry.entity.AssetMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetMovementRepository extends JpaRepository<AssetMovement, Long> { }
