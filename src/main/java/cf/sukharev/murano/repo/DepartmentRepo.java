package cf.sukharev.murano.repo;

import cf.sukharev.murano.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Long> {
}
