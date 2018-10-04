package cf.sukharev.murano;

import cf.sukharev.murano.web.EmployeeEndpoint;
import cf.sukharev.murano.web.dto.ChangeEmployeeDepartmentRequest;
import cf.sukharev.murano.web.dto.EmployeeDTO;
import cf.sukharev.murano.web.dto.ListEmployeesRequest;
import cf.sukharev.murano.web.dto.RemoveEmployeeFromDepartmentRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeDepartmentTest {

    private static final long TEST_EMPLOYEE_ID = 1L;

    @Autowired
    private EmployeeEndpoint employeeEndpoint;

    @Test
    @Transactional
    public void youCanChangeEmployeeDepartment() {
        ChangeEmployeeDepartmentRequest request = new ChangeEmployeeDepartmentRequest();
        request.setEmployeeId(TEST_EMPLOYEE_ID);
        request.setNewDepartmentId(2L);
        employeeEndpoint.changeEmployeeDepartment(request);

        ListEmployeesRequest listEmployeesRequest = new ListEmployeesRequest();
        listEmployeesRequest.setDepartmentId(2L);
        List<EmployeeDTO> employee = employeeEndpoint.listEmployees(listEmployeesRequest).getEmployee();
        assertThat(employee)
                .hasSize(3)
                .extracting(EmployeeDTO::getFirstName)
                .contains("John");
    }

    @Test
    @Transactional
    public void youCanRemoveEmployeeFromDepartment() {
        RemoveEmployeeFromDepartmentRequest request = new RemoveEmployeeFromDepartmentRequest();
        request.setEmployeeId(TEST_EMPLOYEE_ID);
        employeeEndpoint.removeEmployeeFromDepartment(request);

        EmployeeDTO dto = employeeEndpoint.listEmployees(new ListEmployeesRequest()).getEmployee().stream()
                .filter(e -> e.getId().equals(TEST_EMPLOYEE_ID))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        assertThat(dto).extracting(EmployeeDTO::getDepartmentId)
                .first().isNull();
    }
}
