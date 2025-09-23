package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import net.psv73.assetregistry.entity.Asset;
import net.psv73.assetregistry.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import net.psv73.assetregistry.web.ApiRoutes.Assets;
import static net.psv73.assetregistry.web.ApiRoutes.Assets.*;

@RestController
@RequestMapping(Assets.ROOT)
@RequiredArgsConstructor
public class AssetController {

    private final AssetRepository assets;

    @GetMapping
    public Page<Asset> list(Pageable pageable) {
        return assets.findAll(pageable);
    }

    @GetMapping(BY_ID)
    public ResponseEntity<Asset> get(@PathVariable Long id) {
        return assets.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Asset> create(@RequestBody Asset body) {
        Asset saved = assets.save(body);
        return ResponseEntity.created(URI.create("/api/assets/" + saved.getId())).body(saved);
    }

    @PutMapping(BY_ID)
    public ResponseEntity<Asset> update(@PathVariable Long id, @RequestBody Asset body) {
        return assets.findById(id).map(existing -> {
            body.setId(id);
            return ResponseEntity.ok(assets.save(body));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(BY_ID)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (assets.existsById(id)) {
            assets.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
