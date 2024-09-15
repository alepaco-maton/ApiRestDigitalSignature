package bo.digitalsignature.infrastructure.persistence.repository;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
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
public class DsUserRepository implements IDsUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public DsUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public DsUser save(DsUser dsUser) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DsUserEntity> cq = cb.createQuery(DsUserEntity.class);
        Root<DsUserEntity> root = cq.from(DsUserEntity.class);


        DsUserEntity entity = new DsUserEntity();
        entity.setUserName(dsUser.getUserName());
        entity.setPrivateKey(dsUser.getPrivateKey());
        entity.setPublicKey(dsUser.getPublicKey());

        entityManager.persist(entity);

        dsUser.setId(entity.getId());

        return dsUser;
    }

    @Override
    @Transactional
    public DsUser update(DsUser dsUser) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DsUserEntity> cq = cb.createQuery(DsUserEntity.class);
        Root<DsUserEntity> root = cq.from(DsUserEntity.class);

        Predicate predicate = cb.equal(root.get("id"), dsUser.getId());

        DsUserEntity entity = entityManager.createQuery(cq.where(predicate)).getSingleResult();
        entity.setUserName(dsUser.getUserName());
        entity.setCert(dsUser.getCert());
        entity.setPrivateKey(dsUser.getPrivateKey());
        entity.setPublicKey(dsUser.getPublicKey());

        return dsUser;
    }

    @Override
    public List<DsUser> list(String userName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DsUserEntity> criteriaQuery = criteriaBuilder.createQuery(DsUserEntity.class);
        Root<DsUserEntity> queryRoot = criteriaQuery.from(DsUserEntity.class);

        Predicate predicate = buildPredicate(criteriaBuilder, queryRoot, userName);

        final String sortBy = "userName";
        final String sortDirection = "ASC";

        criteriaQuery.orderBy(buildOrderClause(criteriaBuilder, queryRoot, sortBy, sortDirection));

        criteriaQuery.select(queryRoot).where(predicate);

        TypedQuery<DsUserEntity> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList().stream()
                .map(entity -> new DsUser(entity.getId(), entity.getUserName(),
                        entity.getCert(), entity.getPrivateKey(), entity.getPublicKey()))
                .collect(Collectors.toList());
    }

    @Override
    public DsUser findById(Integer id) {
        DsUserEntity entity = findEntityById(id);

        if(entity == null)
            return null;

        return new DsUser(entity.getId(), entity.getUserName(),
                entity.getCert(), entity.getPrivateKey(), entity.getPublicKey());
    }

    public DsUserEntity findEntityById(Integer id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DsUserEntity> criteriaQuery = criteriaBuilder.createQuery(DsUserEntity.class);
        Root<DsUserEntity> queryRoot = criteriaQuery.from(DsUserEntity.class);

        Predicate predicate = criteriaBuilder.conjunction();
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(queryRoot.get("id").as(String.class), "%" + id.toString().trim().toUpperCase() + "%"));

        criteriaQuery.select(queryRoot).where(predicate);

        TypedQuery<DsUserEntity> query = entityManager.createQuery(criteriaQuery);

        List<DsUserEntity> list = query.getResultList();

        if(list.isEmpty())
            return null;

        return list.get(0);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<DsUserEntity> delete = cb.createCriteriaDelete(DsUserEntity.class);
        Root<DsUserEntity> root = delete.from(DsUserEntity.class);

        // Construimos el predicado para encontrar la entidad por su ID
        Predicate predicate = cb.equal(root.get("id"), id);
        delete.where(predicate);

        entityManager.createQuery(delete).executeUpdate();
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<DsUserEntity> root, String fullName) {
        Predicate predicate = cb.conjunction();

        // Add predicates based on provided parameters
        if (!AppTools.isBlank(fullName)) {
            predicate = cb.and(predicate, cb.like(cb.upper(root.get("userName")), "%" + fullName.trim().toUpperCase() + "%"));
        }

        return predicate;
    }

    private Order buildOrderClause(CriteriaBuilder cb, Root<DsUserEntity> root, String sortBy, String sortDirection) {
        switch (sortBy) {
            case "userName":
                return sortDirection.equals("ASC") ? cb.asc(root.get("userName")) : cb.desc(root.get("userName"));
            default:
                return cb.desc(root.get("id"));
        }
    }

}
