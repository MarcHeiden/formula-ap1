package f1api.driver.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;
import static java.util.Map.entry;

import f1api.driver.domain.Driver;
import f1api.driver.domain.DriverRepository;
import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.exception.ApiPropertyIsNullException;
import f1api.responsepage.ResponsePage;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class DriverApplicationService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverQueryParameter driverQueryParameter;

    @Autowired
    public DriverApplicationService(
            DriverRepository driverRepository, DriverMapper driverMapper, DriverQueryParameter driverQueryParameter) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.driverQueryParameter = driverQueryParameter;
    }

    private void checkIfDriverAlreadyExists(String firstName, String lastName) {
        if (driverRepository.existsDriverByFirstNameAndLastName(firstName, lastName)) {
            throw ApiInstanceAlreadyExistsException.of(
                    "Driver", Map.ofEntries(entry("firstName", firstName), entry("lastName", lastName)));
        }
    }

    public DriverDTO createDriver(DriverDTO driverDTO) {
        checkIfDriverAlreadyExists(driverDTO.getFirstName(), driverDTO.getLastName());
        Driver driver = driverMapper.toDriver(driverDTO);
        driverRepository.save(driver);
        return driverMapper.toDriverDTO(driver);
    }

    public ResponsePage<DriverDTO> getDrivers(
            Pageable pageable, String firstName, String lastName, MultiValueMap<String, String> parameters) {
        handleQueryParameters(parameters, driverQueryParameter);
        Page<Driver> drivers;
        if (firstName != null && lastName != null) {
            drivers = driverRepository.findDriverByFirstNameAndLastName(pageable, firstName, lastName);
        } else if (firstName != null) {
            drivers = driverRepository.findDriversByFirstName(pageable, firstName);
        } else if (lastName != null) {
            drivers = driverRepository.findDriversByLastName(pageable, lastName);
        } else {
            drivers = driverRepository.findAll(pageable);
        }
        return ResponsePage.of(drivers.map(driverMapper::toDriverDTO));
    }

    public Driver getDriverById(UUID id) {
        return driverRepository
                .findById(id)
                .orElseThrow(() -> ApiNotFoundException.of("Driver", "driverId", id.toString()));
    }

    public DriverDTO getDriver(UUID driverId) {
        return driverMapper.toDriverDTO(getDriverById(driverId));
    }

    public DriverDTO updateDriver(UUID driverId, DriverDTO driverDTO) {
        Driver driver = getDriverById(driverId);
        if (driverDTO.isEmpty()) {
            throw ApiPropertyIsNullException.of(DriverDTO.getNotNullProperties());
        }
        if (driverDTO.getFirstName() != null && driverDTO.getLastName() != null) {
            checkIfDriverAlreadyExists(driverDTO.getFirstName(), driverDTO.getLastName());
        }
        if (driverDTO.getFirstName() != null) {
            driver.setFirstName(driverDTO.getFirstName());
        }
        if (driverDTO.getLastName() != null) {
            driver.setLastName(driverDTO.getLastName());
        }
        driverRepository.save(driver);
        return driverMapper.toDriverDTO(driver);
    }
}
