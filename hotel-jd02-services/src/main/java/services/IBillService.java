package services;


import pojos.Bill;
import pojos.enums.BillStatusType;

import java.io.Serializable;
import java.util.List;

public interface IBillService extends IService<Bill> {
    /**
     * @param billId determine id of Bill
     * @return true if Bill successfully paid;
     * return false if no such Bill in DB, or Bill already paid, or Order that correspondent
     * to the bill not found in DB
     */
    boolean billPay(Serializable billId);

    /**
     * @param userId determine id of User who must pay Bill
     * @return list of all Bills for User with id = userId,
     * if not one found - return empty List
     */

    List<Bill> getAllForUserId(Serializable userId);

    /**
     * @param billStatus determine Status of Bill
     * @param userId determine id of User who must pay Bill
     * @return return all Bills for User with id = userId and BillStatusType = billStatus,
     * if not one found - return empty List
     */
    List<Bill> getAllForStatusAndUser(BillStatusType billStatus, Serializable userId);

    /**
     *
     * @param billStatus determine Status of Bill
     * @param hotelId determine id of Hotel that billed the bill
     * @return return all Bills for Hotel with id = hotelId and BillStatusType = billStatus,
     * if not one found - return empty List
     */
    List<Bill> getAllForStatusAndHotel(BillStatusType billStatus, Serializable hotelId);
}
