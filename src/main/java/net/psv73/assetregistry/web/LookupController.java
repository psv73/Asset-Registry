package net.psv73.assetregistry.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import net.psv73.assetregistry.repository.*;
import net.psv73.assetregistry.entity.*;

import net.psv73.assetregistry.web.ApiRoutes.Lookups;
import static net.psv73.assetregistry.web.ApiRoutes.Lookups.*;

@RestController
@RequestMapping(Lookups.ROOT)
@RequiredArgsConstructor
public class LookupController {

    private final AssetTypeRepository assetTypes;
    private final ManufacturerRepository manufacturers;
    private final ModelRepository models;
    private final DeviceTypeRepository deviceTypes;
    private final OsRepository oses;
    private final LocationRepository locations;
    private final OfficeRepository offices;
    private final StatusRepository statuses;
    private final EmployeeRepository employees;

    @GetMapping(ASSET_TYPES) public List<AssetType> assetTypes() { return assetTypes.findAll(); }
    @GetMapping(MANUFACTURERS) public List<Manufacturer> manufacturers() { return manufacturers.findAll(); }
    @GetMapping(MODELS) public List<Model> models() { return models.findAll(); }
    @GetMapping(DEVICE_TYPES) public List<DeviceType> deviceTypes() { return deviceTypes.findAll(); }
    @GetMapping(OSES) public List<Os> oses() { return oses.findAll(); }
    @GetMapping(LOCATIONS) public List<Location> locations() { return locations.findAll(); }
    @GetMapping(OFFICES) public List<Office> offices() { return offices.findAll(); }
    @GetMapping(STATUSES) public List<Status> statuses() { return statuses.findAll(); }
    @GetMapping(EMPLOYEES) public List<Employee> employees() { return employees.findAll(); }
}
