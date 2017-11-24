package dao;



import pojos.Bill;
import pojos.enums.BillStatusType;

import java.io.Serializable;
import java.util.List;

public interface IBillDao extends IDao<Bill> {
    /**
     * return all Bills for User with id = userId,
     * if not one found - return empty List
     */
    List<Bill> getAllForUserId(Serializable userId);

    /**
     * return all Bills for User with id = userId and BillStatusType = billStatus,
     * if not one found - return empty List
     */
    List<Bill> getAllForStatusAndUser(BillStatusType billStatus, Serializable userId);

    /**
     * return all Bills for Hotel with id = hotelId and BillStatusType = billStatus,
     * if not one found - return empty List
     */
    List<Bill> getAllForStatusAndHotel(BillStatusType billStatus, Serializable hotelId);
}
