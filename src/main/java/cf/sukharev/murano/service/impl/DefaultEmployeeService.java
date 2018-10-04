package cf.sukharev.murano.service.impl;

import cf.sukharev.murano.model.Employee;
import cf.sukharev.murano.repo.EmployeeRepo;
import cf.sukharev.murano.service.DepartmentService;
import cf.sukharev.murano.service.EmployeeService;
import cf.sukharev.murano.service.exceptions.EntityNotFoundException;
import cf.sukharev.murano.web.dto.EmployeeDTO;
import cf.sukharev.murano.web.dto.UpdateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static cf.sukharev.murano.service.exceptions.ExceptionSuppliers.alreadyExistsException;
import static cf.sukharev.murano.service.exceptions.ExceptionSuppliers.notFoundException;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultEmployeeService implements EmployeeService {
    private final EmployeeRepo employeeRepo;
    private final DepartmentService departmentService;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> findAllEmployees() {
        log.debug("Requesting database for all employees list.");
        return employeeRepo.findAll().stream()
                .map(this::toDto)
                .collect(toList());
    }

    private EmployeeDTO toDto(Employee employee) { // TODO: MapStruct/Spring Converter?
        EmployeeDTO employeeDto = new EmployeeDTO();
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setPhone(employee.getPhone());
        employeeDto.setDepartmentId(employee.getDepartmentId());
        employeeDto.setId(employee.getId());
        return employeeDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> findEmployeesInDepartment(final Long departmentId) {
        log.debug("Requesting database for employees in department {}.", departmentId);
        return employeeRepo.findAllByDepartment_Id(departmentId)
                .stream()
                .map(this::toDto)
                .collect(toList());
    }

    @Override
    public Long createEmployee(final EmployeeDTO dto) {
        log.debug("Creating new employee.");
        log.trace("Employee parameters for creating is {}.", dto);
        if (employeeRepo.existsByPhone(dto.getPhone())) {
            throw alreadyExistsException(format("Employee with phone %s already exists.", dto.getPhone()));
        }
        Employee employee = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .build();
        departmentService.addEmployee(dto.getDepartmentId(), employee);
        return employeeRepo.save(employee).getId();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Performs so-called "soft-deleting", just marking employee as "deleted".
     */
    @Override
    public void deleteEmployee(long employeeId) {
        log.info("Deleting employee {}.", employeeId);
        employeeRepo.markDeleted(employeeId);
    }

    @Override
    public void updateEmployee(long employeeId, final UpdateEmployeeRequest.Fields fields) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(employeeDoesNotExist(employeeId));
        log.debug("Updating employee {} with new fields {}.", employeeId, fields);
        copyNonNullProperties(fields, employee);
        employeeRepo.save(employee);
    }

    @Override
    public void changeEmployeeDepartment(long employeeId, long newDepartmentId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(employeeDoesNotExist(employeeId));
        if (!employee.belongsToDepartment(newDepartmentId)) {
            log.debug("Changing employee {} department from {} to {}.", employeeId, employee.getDepartmentId(), newDepartmentId);
            departmentService.addEmployee(newDepartmentId, employee);
        } else {
            log.debug("Employee {} already in target department, skipping.", employeeId);
        }
    }

    @Override
    public void removeEmployeeFromDepartment(long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(employeeDoesNotExist(employeeId));
        departmentService.removeDepartment(employee);
        employeeRepo.save(employee);
    }

    private Supplier<EntityNotFoundException> employeeDoesNotExist(long employeeId) {
        return notFoundException(format("Employee with id %s does not exist.", employeeId));
    }

    private <T, R> void copyNonNullProperties(T src, R target) {
        BeanWrapper wrappedSrc = new BeanWrapperImpl(src);
        PropertyDescriptor[] propertyDescriptors = wrappedSrc.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object srcValue = null;
            try {
                srcValue = wrappedSrc.getPropertyValue(propertyDescriptor.getName());
            } catch (Exception ignored) {
            }
            if (srcValue == null) emptyNames.add(propertyDescriptor.getName());
        }
        String[] result = new String[emptyNames.size()];
        BeanUtils.copyProperties(src, target, emptyNames.toArray(result));
    }
}
