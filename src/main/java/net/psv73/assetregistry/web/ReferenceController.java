package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import net.psv73.assetregistry.repository.ClientRepository;
import net.psv73.assetregistry.repository.ModelRepository;
import net.psv73.assetregistry.repository.OsRepository;
import net.psv73.assetregistry.repository.StatusRepository;
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

    @GetMapping(ApiPaths.Ref.STATUSES)
    public List<IdNameDto> statuses() {
        return map(statusRepository.findAll(), s -> new IdNameDto(s.getId(), s.getCode()));
    }

    @GetMapping(ApiPaths.Ref.OSES)
    public List<IdNameDto> oses() {
        return map(osRepository.findAll(), o -> new IdNameDto(o.getId(), o.getName()));
    }

    @GetMapping(ApiPaths.Ref.CLIENTS)
    public List<IdNameDto> clients() {
        return map(clientRepository.findAll(), c -> new IdNameDto(c.getId(), c.getName()));
    }

    @GetMapping(ApiPaths.Ref.MODELS)
    public List<IdNameDto> models() {
        return map(modelRepository.findAll(), m -> new IdNameDto(m.getId(), m.getName()));
    }

    @GetMapping(ApiPaths.Ref.MANUFACTURERS)
    public List<IdNameDto> manufacturers() {
        return map(manufacturerRepository.findAll(), m -> new IdNameDto(m.getId(), m.getName()));
    }

    @GetMapping(ApiPaths.Ref.DEVICE_TYPES)
    public List<IdNameDto> deviceTypes() {
        return map(deviceTypeRepository.findAll(), t -> new IdNameDto(t.getId(), t.getName()));
    }
}

