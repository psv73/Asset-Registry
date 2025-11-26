package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import net.psv73.assetregistry.repository.*;
import net.psv73.assetregistry.web.response.IdNameDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReferenceController {
    private final StatusRepository statusRepository;
    private final OsRepository osRepository;
    private final ClientRepository clientRepository;
    private final ModelRepository modelRepository;
    private final net.psv73.assetregistry.repository.ManufacturerRepository manufacturerRepository;
    private final net.psv73.assetregistry.repository.DeviceTypeRepository deviceTypeRepository;

    private static <T> List<IdNameDto> map(List<? extends T> list,
                                           java.util.function.Function<T, IdNameDto> f) {
        return list.stream().map(f).sorted(Comparator.comparing(IdNameDto::name)).toList();
    }

    @GetMapping("/api/v1/statuses")
    public List<IdNameDto> statuses() {
        return map(statusRepository.findAll(), s -> new IdNameDto(s.getId(), s.getCode()));
    }

    @GetMapping("/api/v1/oses")
    public List<IdNameDto> oses() {
        return map(osRepository.findAll(), o -> new IdNameDto(o.getId(), o.getName()));
    }

    @GetMapping("/api/v1/clients")
    public List<IdNameDto> clients() {
        return map(clientRepository.findAll(), c -> new IdNameDto(c.getId(), c.getName()));
    }

    @GetMapping("/api/v1/models")
    public List<IdNameDto> models() {
        return map(modelRepository.findAll(), m -> new IdNameDto(m.getId(), m.getName()));
    }

    @GetMapping("/api/v1/manufacturers")
    public List<IdNameDto> manufacturers() {
        return map(manufacturerRepository.findAll(), m -> new IdNameDto(m.getId(), m.getName()));
    }

    @GetMapping("/api/v1/device-types")
    public List<IdNameDto> deviceTypes() {
        return map(deviceTypeRepository.findAll(), t -> new IdNameDto(t.getId(), t.getName()));
    }
}

