package paging;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class EntityJPA_PagingUtil {
    private <T> Long numberOffElements(EntityManager em, Class<T> classEntity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        criteria.select(cb.count(criteria.from(classEntity)));
        return em.createQuery(criteria).getSingleResult();
    }

    public <T> List<T> getPageList(Class<T> classEntity, EntityManager em,
//                                   List<Order> orderList,
                                   CriteriaQuery<T> criteria, int pageSize, int pageNumber) {
        //check correct inner parameters
        if (em == null) {
            return new ArrayList<T>();
        }

        Integer numberOffElements = Integer.valueOf(numberOffElements(em, classEntity).toString());
        if (pageSize <= 0) {
            pageSize = 1;
        }

        int maxPageNumber = numberOffElements % pageSize == 0
                ? numberOffElements / pageSize
                : numberOffElements / pageSize + 1;
        if (pageNumber <= 0) {
            pageNumber = 1;
        } else if (pageNumber > maxPageNumber) {
            pageNumber = maxPageNumber;
        }

        //prepare result List
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<T> criteria = cb.createQuery(classEntity);
//        Root<T> root = criteria.from(classEntity);
//        criteria.select(root);
//        //???
//        criteria.orderBy(orderList);

        TypedQuery<T> typedQuery = em.createQuery(criteria);
        typedQuery.setFirstResult(pageSize * (pageNumber - 1));
        typedQuery.setMaxResults(pageSize);
        List<T> result = typedQuery.getResultList();

        return result;
    }
}
