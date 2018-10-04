package cf.sukharev.murano.service.impl;

import cf.sukharev.murano.model.Department;
import cf.sukharev.murano.model.Employee;
import cf.sukharev.murano.repo.DepartmentRepo;
import cf.sukharev.murano.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static cf.sukharev.murano.service.exceptions.ExceptionSuppliers.notFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultDepartmentService implements DepartmentService {
    private final DepartmentRepo departmentRepo;

    @Override
    public void addEmployee(long departmentId, final Employee employee) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(notFoundException(String.format("Department with id %s does not exist.", departmentId)));
        log.debug("Adding employee {} to department {}", employee, departmentId);
        department.addEmployee(employee);
        departmentRepo.save(department);
    }

    @Override
    public void removeDepartment(final Employee employee) {
        Optional.ofNullable(employee.getDepartment())
                .ifPresent(department -> {
                    department.removeEmployee(employee);
                    departmentRepo.save(department);
                });
    }
}
