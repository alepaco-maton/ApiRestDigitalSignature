package bo.digitalsignature.infrastructure.persistence.repository;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.infrastructure.api.dto.dsuser.ListDsUserDsDocumentResponse;
import bo.digitalsignature.infrastructure.persistence.entity.DsDocumentEntity;
import bo.digitalsignature.infrastructure.persistence.entity.DsUserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DsDocumentRepository implements IDsDocumentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public DsDocumentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ListDsUserDsDocumentResponse> findByDsUserId(Integer dsUserId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DsDocumentEntity> criteriaQuery = criteriaBuilder.createQuery(DsDocumentEntity.class);
        Root<DsDocumentEntity> queryRoot = criteriaQuery.from(DsDocumentEntity.class);

        Predicate predicate = buildPredicate(criteriaBuilder, queryRoot, dsUserId.toString());

        final String sortBy = "fileName";
        final String sortDirection = "ASC";

        criteriaQuery.orderBy(buildOrderClause(criteriaBuilder, queryRoot, sortBy, sortDirection));

        criteriaQuery.select(queryRoot).where(predicate);

        TypedQuery<DsDocumentEntity> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList().stream()
                .map(entity -> new ListDsUserDsDocumentResponse(
                        entity.getId(), entity.getFileName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllByDsUserId(int id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<DsDocumentEntity> delete = cb.createCriteriaDelete(DsDocumentEntity.class);
        Root<DsDocumentEntity> root = delete.from(DsDocumentEntity.class);

        // Construimos el predicado para encontrar la entidad por su ID
        Predicate predicate = cb.equal(root.get("id"), id);
        delete.where(predicate);

        entityManager.createQuery(delete).executeUpdate();
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<DsDocumentEntity> root, String dsUserId) {
        Predicate predicate = cb.conjunction();

        // Add predicates based on provided parameters
        if (!AppTools.isBlank(dsUserId)) {
            predicate = cb.and(predicate, cb.like(root.get("user").get("id").as(String.class), "%" + dsUserId.trim().toUpperCase() + "%"));
        }

        return predicate;
    }

    private Order buildOrderClause(CriteriaBuilder cb, Root<DsDocumentEntity> root, String sortBy, String sortDirection) {
        switch (sortBy) {
            case "fileName":
                return sortDirection.equals("ASC") ? cb.asc(root.get("fileName")) : cb.desc(root.get("fileName"));
            default:
                return cb.desc(root.get("id"));
        }
    }
}
