package cf.sukharev.murano.repo.custom;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

@NoRepositoryBean
public interface DeleteAwareBaseRepository <T, ID> extends Repository<T, ID> {

    List<T> findAllNotDeleted();

    List<T> findAllNotDeletedInDepartment(Long departmentId);
}
