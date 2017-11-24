package dao.impl;

import dao.IDao;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Repository()
public class BaseDao<T> implements IDao<T> {
    private static Logger log = Logger.getLogger(BaseDao.class);

    @PersistenceContext
    @Getter
    private EntityManager em;

    Class<T> clazz;


    @Override
    public T add(T entity) {
        em.persist(entity);
        log.info("Save: " + entity);
        return entity;
    }

    @Override
    public T update(T entity) {
//        getEntityManager().flush();
        em.merge(entity);

        log.info("Update: " + entity);
        return entity;
    }

    @Override
    public T get(Serializable entityId) {
        // return null if entity not exist
        T entity = em.find(this.clazz, entityId);

        log.info(String.format("Get entity [%s] with id [%s]. Entity: %s",
                this.clazz, entityId, entity));

        return entity;
    }

    @Override
    public void delete(Serializable entityId) {
        T findEntity = this.get(entityId);

        log.info("Delete: " + findEntity);
        em.remove(findEntity);
    }

    @Override
    public void refresh(T entity) {
        log.info("Refresh: " + entity);
        em.refresh(entity);
    }

    CriteriaQuery<T> getCriteriaQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> criteria = cb.createQuery(this.clazz);

        Root<T> root = criteria.from(this.clazz);
        criteria.select(root);

        return criteria;
    }
    T getSingleResultWhere(CriteriaQuery<T> criteria) {
        return em.createQuery(criteria).getSingleResult();
    }

    List<T> getListWhere(CriteriaQuery<T> criteria) {

        return em.createQuery(criteria).getResultList();
    }

    @Override
    public List<T> getAll() {
        List<T> resultList = em.createQuery(getCriteriaQuery()).getResultList();
        log.info("Get all entities: " + resultList);

        return resultList;
    }
}
