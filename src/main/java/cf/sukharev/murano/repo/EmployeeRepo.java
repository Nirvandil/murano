package cf.sukharev.murano.repo;

import cf.sukharev.murano.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    List<Employee> findAllByDepartment_Id(Long departmentId);

    @Query("UPDATE Employee e SET e.deleted = TRUE WHERE id = ?1")
    @Modifying
    void markDeleted(long employeeId);

    boolean existsByPhone(String phone);
}
