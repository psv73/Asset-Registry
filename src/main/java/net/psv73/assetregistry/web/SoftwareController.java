package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import net.psv73.assetregistry.repository.*;
import net.psv73.assetregistry.entity.*;

import java.net.URI;
import java.util.List;

import net.psv73.assetregistry.web.ApiRoutes.Software;
import static net.psv73.assetregistry.web.ApiRoutes.Software.*;


@RestController
@RequestMapping(Software.ROOT)
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareProductRepository products;
    private final AssetSoftwareRepository assetSoftware;

    @GetMapping(PRODUCTS)
    public Page<SoftwareProduct> listProducts(Pageable pageable) {
        return products.findAll(pageable);
    }

    @PostMapping(PRODUCTS)
    public ResponseEntity<SoftwareProduct> createProduct(@RequestBody SoftwareProduct body) {
        SoftwareProduct saved = products.save(body);
        return ResponseEntity.created(URI.create("/api/software/products/" + saved.getId())).body(saved);
    }

    @GetMapping(SW_ON_ASSET)
    public List<AssetSoftware> softwareOnAsset(@PathVariable Long assetId) {
        return assetSoftware.findAll().stream()
                .filter(x -> x.getAsset() != null && x.getAsset().getId().equals(assetId))
                .toList();
    }

    @PostMapping(ASSIGN)
    public ResponseEntity<AssetSoftware> assignToAsset(@RequestBody AssetSoftware body) {
        AssetSoftware saved = assetSoftware.save(body);
        return ResponseEntity.created(URI.create("/api/software/assign/" + saved.getId())).body(saved);
    }
}
