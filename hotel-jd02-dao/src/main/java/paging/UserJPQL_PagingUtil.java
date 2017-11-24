package paging;

import pojos.User;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class UserJPQL_PagingUtil {
    private Long numberOffElements(EntityManager em) {
        String jpql = "SELECT COUNT (Ch) FROM User AS Ch";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    public List<User> getPageList(EntityManager em, int elementsPearPage, int pageNumber) {
        //check correct inner parameters
        if (em == null) {
            return new ArrayList<>();
        }

        Integer numberOffElements = Integer.valueOf(numberOffElements(em).toString()) ;
        if (elementsPearPage <= 0 ) {
            elementsPearPage = 1;
        }

        int maxPageNumber = numberOffElements % elementsPearPage == 0
                ? numberOffElements / elementsPearPage
                : numberOffElements / elementsPearPage + 1;
        if (pageNumber <= 0) {
            pageNumber = 1;
        } else if (pageNumber > maxPageNumber) {
            pageNumber = maxPageNumber;
        }

        //prepare result List
        String jpql = "SELECT Ch FROM User AS Ch";
        List<User> result = em.createQuery(jpql, User.class)
                               .setFirstResult(elementsPearPage * (pageNumber - 1))
                               .setMaxResults(elementsPearPage)
                               .getResultList();

        return result;

    }


}
