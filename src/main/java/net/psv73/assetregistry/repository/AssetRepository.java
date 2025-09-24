package net.psv73.assetregistry.repository;
import net.psv73.assetregistry.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface AssetRepository extends JpaRepository<Asset, Long> {
  @Query("select a from Asset a where a.client.id = :clientId")
  List<Asset> findAllByClientId(@Param("clientId") Long clientId);
}