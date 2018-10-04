package cf.sukharev.murano.repo.custom;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DeleteAwareBaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements DeleteAwareBaseRepository<T, ID> {
    private EntityManager entityManager;
    private Class<T> domainClass;


    public DeleteAwareBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.domainClass = entityInformation.getJavaType();
    }

    public DeleteAwareBaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
        this.domainClass = domainClass;
    }

    @Override
    @Transactional
    public List<T> findAllNotDeleted() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(domainClass);
        Root<T> from = query.from(domainClass);
        query.select(from);
        return entityManager.createQuery(query.where(criteriaBuilder.isFalse(from.get("deleted")))).getResultList();
    }

    @Override
    public List<T> findAllNotDeletedInDepartment(Long departmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(domainClass);
        Root<T> from = query.from(domainClass);
        query.select(from);
        return entityManager.createQuery(query
                .where(cb.and(
                        cb.equal(from.get("department"), departmentId)),
                        cb.isFalse(from.get("deleted"))
                ))
                .getResultList();
    }
}
