package cf.sukharev.murano;

import cf.sukharev.murano.web.EmployeeEndpoint;
import cf.sukharev.murano.web.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = AFTER_CLASS)
public class CRUDEmployeesTest {

    private static final int EMPLOYEES_COUNT_IN_FIRST_DEPARTMENT = 3;
    private static final long FIRST_DEPARTMENT_ID = 1L;
    private static final long SECOND_DEPARTMENT_ID = 2L;
    private static final long TEST_EMPLOYEE_ID = 1L;
    private static final String TEST_EMPLOYEE_PHONE = "(978)-765-07-92";
    private static final String TEST_EMPLOYEE_LAST_NAME = "Doe";
    private final static int TOTAL_EMPLOYEES = 5;

    @Autowired
    private EmployeeEndpoint employeeEndpoint;

    @Test
    @Transactional
    public void youCanCreateEmployee() {
        EmployeeDTO dto = createEmployeeDto();
        CreateEmployeeResponse response = employeeEndpoint.createEmployee(dto);
        assertThat(response.getEmployeeId()).isNotNull().isGreaterThanOrEqualTo(100);
    }

    private EmployeeDTO createEmployeeDto() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setDepartmentId(1L);
        dto.setFirstName("SomeName");
        dto.setLastName("SomeLastName");
        dto.setPhone("(123)-111-22-33");
        return dto;
    }

    @Test
    public void youCanFindAllEmployees() {
        ListEmployeesResponse response = employeeEndpoint.listEmployees(new ListEmployeesRequest());
        assertThat(response).isNotNull();
        assertThat(response.getEmployee())
                .isNotNull()
                .hasSize(TOTAL_EMPLOYEES)
                .extracting(EmployeeDTO::getDepartmentId).containsOnly(FIRST_DEPARTMENT_ID, SECOND_DEPARTMENT_ID);
    }

    @Test
    public void youCanFilterEmployeesByDepartmentId() {
        ListEmployeesRequest request = new ListEmployeesRequest();
        request.setDepartmentId(FIRST_DEPARTMENT_ID);
        ListEmployeesResponse response = employeeEndpoint.listEmployees(request);
        assertThat(response.getEmployee())
                .isNotNull()
                .hasSize(EMPLOYEES_COUNT_IN_FIRST_DEPARTMENT)
                .extracting(EmployeeDTO::getDepartmentId).containsOnly(1L);
    }

    @Test
    public void youCanDeleteEmployee() {
        ListEmployeesRequest listEmployeesRequest = new ListEmployeesRequest();
        ListEmployeesResponse response = employeeEndpoint.listEmployees(listEmployeesRequest);
        assertThat(response.getEmployee().size()).isEqualTo(TOTAL_EMPLOYEES);

        DeleteEmployeeRequest request = new DeleteEmployeeRequest();
        request.setEmployeeId(TEST_EMPLOYEE_ID);

        employeeEndpoint.deleteEmployee(request);
        response = employeeEndpoint.listEmployees(listEmployeesRequest);
        assertThat(response.getEmployee().size()).isEqualTo(TOTAL_EMPLOYEES - 1);
    }

    @Test
    public void youCanUpdateEmployee() {
        String updatedName = updateEmployeeName();
        EmployeeDTO updatedEmployee = getUpdatedEmployee();

        assertThat(updatedEmployee)
                .matches(emp -> emp.getLastName().equals(TEST_EMPLOYEE_LAST_NAME) &&
                        emp.getPhone().equals(TEST_EMPLOYEE_PHONE) &&
                        emp.getDepartmentId() == FIRST_DEPARTMENT_ID)
                .extracting(EmployeeDTO::getFirstName)
                .containsExactly(updatedName);
    }

    private String updateEmployeeName() {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setEmployeeId(TEST_EMPLOYEE_ID);
        UpdateEmployeeRequest.Fields fields = new UpdateEmployeeRequest.Fields();
        String updatedName = "UpdatedName";
        fields.setFirstName(updatedName);
        request.setFields(fields);
        employeeEndpoint.updateEmployee(request);
        return updatedName;
    }


    private EmployeeDTO getUpdatedEmployee() {
        return employeeEndpoint.listEmployees(new ListEmployeesRequest()).getEmployee().stream()
                .filter(emp -> emp.getId() == TEST_EMPLOYEE_ID)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

}
