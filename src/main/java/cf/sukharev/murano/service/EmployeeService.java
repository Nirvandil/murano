package cf.sukharev.murano.service;

import cf.sukharev.murano.web.dto.EmployeeDTO;
import cf.sukharev.murano.web.dto.UpdateEmployeeRequest;

import java.util.List;

public interface EmployeeService {

    /**
     * Allows to find all employees.
     * @return List of all {@link cf.sukharev.murano.model.Employee Employees}.
     */
    List<EmployeeDTO> findAllEmployees();

    /**
     * Allows to search employees belonging to specified department.
     * @param departmentId department ID.
     * @return List of {@link cf.sukharev.murano.model.Employee Employees} in specified department.
     */
    List<EmployeeDTO> findEmployeesInDepartment(final Long departmentId);

    /**
     * Allows to create new {@link cf.sukharev.murano.model.Employee Employee}.
     * @param request {@link EmployeeDTO} DTO with new employee details.
     * @return ID of newly created employee.
     */
    Long createEmployee(final EmployeeDTO request);

    /**
     * Allows to delete specified {@link cf.sukharev.murano.model.Employee Employee}.
     * @param employeeId ID of employee to be deleted.
     */
    void deleteEmployee(long employeeId);

    /**
     * Allows to update specified {@link cf.sukharev.murano.model.Employee Employee}.
     * @param employeeId ID of employee to be updated.
     * @param fields {@link UpdateEmployeeRequest.Fields Field} with new values for update.
     */
    void updateEmployee(long employeeId, final UpdateEmployeeRequest.Fields fields);

    /**
     * Allows to change current {@link cf.sukharev.murano.model.Employee Employee} {@link cf.sukharev.murano.model.Department department}.
     * @param employeeId ID of employee.
     * @param newDepartmentId ID of new department.
     */
    void changeEmployeeDepartment(long employeeId, long newDepartmentId);

    /**
     * Allows to remove {@link cf.sukharev.murano.model.Employee Employee} from department.
     * Affected employee will have null department id.
     * @param employeeId ID of employee.
     */
    void removeEmployeeFromDepartment(long employeeId);
}
