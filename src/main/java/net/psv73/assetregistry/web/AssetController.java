package net.psv73.assetregistry.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.psv73.assetregistry.entity.*;
import net.psv73.assetregistry.repository.*;
import net.psv73.assetregistry.web.dto.AssetMapper;
import net.psv73.assetregistry.web.request.AssetRequestDto;
import net.psv73.assetregistry.web.response.AssetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AssetController {

    private final AssetRepository assetRepository;
    private final ClientRepository clientRepository;
    private final ModelRepository modelRepository;
    private final StatusRepository statusRepository;
    private final OsRepository osRepository;

    @GetMapping(ApiPaths.Assets.BY_ID)
    public AssetResponseDto getById(@PathVariable("id") Long id) {
        return assetRepository.findById(id)
                .map(AssetMapper::toResponse)
                .orElseThrow(() -> new java.util.NoSuchElementException("Asset not found: " + id));
    }

    @GetMapping(ApiPaths.Assets.ROOT) // GET /api/v1/assets?inventoryCode=INV-001
    public Page<AssetResponseDto> getAll(
            @RequestParam(value = "inventoryCode", required = false) String inventoryCode, Pageable pageable
    ) {
        if (inventoryCode != null && !inventoryCode.isBlank()) {
            return assetRepository.findByInventoryCode(inventoryCode)
                    .map(net.psv73.assetregistry.web.dto.AssetMapper::toResponse)
                    .map(java.util.List::of)
                    .map(PageImpl::new)
                    .orElseGet(() -> new org.springframework.data.domain.PageImpl<>(java.util.List.of()));
        }
        return assetRepository.findAll(pageable).map(net.psv73.assetregistry.web.dto.AssetMapper::toResponse);
    }


    @PostMapping(ApiPaths.Assets.ROOT)
    public ResponseEntity<AssetResponseDto> create(@RequestBody @Valid AssetRequestDto dto) throws Exception {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        Model model = modelRepository.findById(dto.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        Status status = statusRepository.findById(dto.statusId())
                .orElseThrow(() -> new IllegalArgumentException("Status not found"));
        Os os = osRepository.findById(dto.osId())
                .orElseThrow(() -> new IllegalArgumentException("OS not found"));

        InetAddress ip = null;
        if (dto.ip() != null && !dto.ip().isBlank()) {
            ip = InetAddress.getByName(dto.ip()); // бросит UnknownHostException, если криво
        }

        Asset asset = assetRepository.save(Asset.builder()
                .client(client)
                .model(model)
                .status(status)
                .os(os)
                // остальные поля из dto
                .inventoryCode(dto.inventoryCode())
                .serialNumber(dto.serialNumber())
                .hostname(dto.hostname())
                .ip(ip)
                .dhcp(dto.dhcp())
                .purchaseDate(dto.purchaseDate())
                .params(dto.params())
                .note(dto.note())
                .build());

        Asset saved = assetRepository.save(asset);
        return ResponseEntity
                .created(URI.create(ApiPaths.Assets.ROOT + "/" + saved.getId()))
                .body(AssetMapper.toResponse(saved));
    }

    @PutMapping(ApiPaths.Assets.BY_ID)
    public AssetResponseDto update(@PathVariable Long id,
                                   @RequestBody @Valid AssetRequestDto dto) throws Exception {
        Asset existing = assetRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Asset not found: " + id));

        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + dto.clientId()));
        Model model = modelRepository.findById(dto.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + dto.modelId()));
        Status status = statusRepository.findById(dto.statusId())
                .orElseThrow(() -> new IllegalArgumentException("Status not found: " + dto.statusId()));
        Os os = osRepository.findById(dto.osId())
                .orElseThrow(() -> new IllegalArgumentException("OS not found: " + dto.osId()));

        java.net.InetAddress ip = (dto.ip() == null || dto.ip().isBlank())
                ? null : java.net.InetAddress.getByName(dto.ip());

        existing.setClient(client);
        existing.setModel(model);
        existing.setStatus(status);
        existing.setOs(os);
        existing.setInventoryCode(dto.inventoryCode());
        existing.setSerialNumber(dto.serialNumber());
        existing.setHostname(dto.hostname());
        existing.setIp(ip);
        existing.setDhcp(dto.dhcp());
        existing.setPurchaseDate(dto.purchaseDate());
        existing.setParams(dto.params());
        existing.setNote(dto.note());

        return AssetMapper.toResponse(assetRepository.save(existing));
    }

    @DeleteMapping(ApiPaths.Assets.BY_ID)
    public org.springframework.http.ResponseEntity<Void> delete(@PathVariable Long id) {
        var asset = assetRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Asset not found: " + id));
        assetRepository.delete(asset);
        return ResponseEntity.noContent().build(); // 204
    }

}
