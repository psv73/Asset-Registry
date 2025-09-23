package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import net.psv73.assetregistry.repository.*;
import net.psv73.assetregistry.entity.*;

import net.psv73.assetregistry.web.ApiRoutes.Movements;
import static net.psv73.assetregistry.web.ApiRoutes.Movements.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Movements.ROOT)
@RequiredArgsConstructor
public class AssetMovementController {

    private final AssetMovementRepository movements;
    private final AssetRepository assets;

    @GetMapping
    public Page<AssetMovement> list(Pageable pageable) {
        return movements.findAll(pageable);
    }

    @GetMapping(BY_ASSET)
    public List<AssetMovement> byAsset(@PathVariable Long assetId) {
        // For simplicity without custom repo method: filter in memory would be bad,
        // but typically we'd add a query method. Assume small demo; in real code add:
        // List<AssetMovement> findByAssetIdOrderByEffectiveFromDesc(Long assetId);
        return movements.findAll().stream()
                .filter(m -> m.getAsset() != null && m.getAsset().getId().equals(assetId))
                .toList();
    }

    @PostMapping
    public ResponseEntity<AssetMovement> create(@RequestBody AssetMovement body) {
        AssetMovement saved = movements.save(body);
        return ResponseEntity.created(URI.create(Movements.ROOT + saved.getId())).body(saved);
    }
}
