package net.psv73.assetregistry.repository;

import net.psv73.assetregistry.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {}