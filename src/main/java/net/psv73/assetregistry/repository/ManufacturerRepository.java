package net.psv73.assetregistry.repository;

import net.psv73.assetregistry.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    default List<Manufacturer> findAllSorted() {
        return findAll(Sort.by("name").ascending());
    }
}
