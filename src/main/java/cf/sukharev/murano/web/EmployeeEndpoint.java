package cf.sukharev.murano.web;

import cf.sukharev.murano.service.EmployeeService;
import cf.sukharev.murano.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static cf.sukharev.murano.MuranoApplication.MURANO_NAMESPACE;

@Slf4j
@Endpoint
@RequiredArgsConstructor
public class EmployeeEndpoint {
    private final EmployeeService employeeService;

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "ListEmployeesRequest")
    @ResponsePayload
    public ListEmployeesResponse listEmployees(@RequestPayload ListEmployeesRequest request) {
        log.debug("Received request for list employees.");
        Long departmentId = request.getDepartmentId();
        ListEmployeesResponse response = new ListEmployeesResponse();
        if (departmentId != null) {
            response.getEmployee().addAll(employeeService.findEmployeesInDepartment(departmentId));
        } else {
            response.getEmployee().addAll(employeeService.findAllEmployees());
        }
        return response;
    }

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "CreateEmployeeRequest")
    @ResponsePayload
    public CreateEmployeeResponse createEmployee(@RequestPayload EmployeeDTO request) {
        log.debug("Received request for create employee.");
        Long employeeId = employeeService.createEmployee(request);
        CreateEmployeeResponse response = new CreateEmployeeResponse();
        response.setEmployeeId(employeeId);
        return response;
    }

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "DeleteEmployeeRequest")
    @ResponsePayload
    public void deleteEmployee(@RequestPayload DeleteEmployeeRequest request) {
        log.info("Received request for deleting employee with id {}.", request.getEmployeeId());
        employeeService.deleteEmployee(request.getEmployeeId());
    }

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "UpdateEmployeeRequest")
    @ResponsePayload
    public void updateEmployee(@RequestPayload UpdateEmployeeRequest request) {
        log.debug("Received request for updating employee {}.", request.getEmployeeId());
        employeeService.updateEmployee(request.getEmployeeId(), request.getFields());
    }

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "ChangeEmployeeDepartmentRequest")
    @ResponsePayload
    public void changeEmployeeDepartment(@RequestPayload ChangeEmployeeDepartmentRequest request) {
        long employeeId = request.getEmployeeId();
        long newDepartmentId = request.getNewDepartmentId();
        log.debug("Received request for change employee {} to new department {}", employeeId, newDepartmentId);
        employeeService.changeEmployeeDepartment(employeeId, newDepartmentId);
    }

    @PayloadRoot(namespace = MURANO_NAMESPACE, localPart = "RemoveEmployeeFromDepartmentRequest")
    @ResponsePayload
    public void removeEmployeeFromDepartment(@RequestPayload RemoveEmployeeFromDepartmentRequest request) {
        log.debug("Received request for removing employee {} department.", request.getEmployeeId());
        employeeService.removeEmployeeFromDepartment(request.getEmployeeId());
    }
}
