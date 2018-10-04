package cf.sukharev.murano.service;

import cf.sukharev.murano.model.Employee;

public interface DepartmentService {
    void addEmployee(long departmentId, final Employee employee);

    void removeDepartment(final Employee employee);
}
