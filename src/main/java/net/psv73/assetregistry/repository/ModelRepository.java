package net.psv73.assetregistry.repository;

import net.psv73.assetregistry.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {}