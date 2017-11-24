package services.impl;

import dao.IBillDao;
import dao.IOrderDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojos.Bill;
import pojos.Order;
import pojos.enums.BillStatusType;
import pojos.enums.OrderStatusType;
import services.IBillService;

import java.io.Serializable;
import java.util.List;

@Service
@Transactional(transactionManager = "txManager")
public class BillService extends BaseService<Bill> implements IBillService {
    private static Logger log = Logger.getLogger(BillService.class);

    @Autowired
    private IBillDao billDao;

    @Autowired
    private IOrderDao orderDao;


    public BillService() {
        super();
    }

    /**
     * @param billId determine id of Bill
     * @return true if Bill successfully paid;
     * return false if no Bill with such id in DB, or Bill already paid, or Order that
     * correspondent to the bill not found in DB
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean billPay(Serializable billId) {
        Bill bill = billDao.get(billId);
        // if No Bill with billId in DB
        if (bill == null ) {
            log.info("Can't pay Bill with id [" + billId + "]. No such Bill in DB");
            return false;
        }

        // bill already paid, nothing to do
        if (BillStatusType.PAID.equals(bill.getStatus())) {
            log.info("Already paid Bill with id [" + billId + "]. Nothing to do");

            return false;
        }

        Order order = orderDao.get(bill.getOrder().getId());
        // if No Order in DB correspondent to the Bill with id=billId
        if ( order == null) {
            log.info("Can't pay Bill with id [" + billId + "]. No one Order correspondent to this bill");
            return false;
        }

        // update bill status
        bill.setStatus(BillStatusType.PAID);
        billDao.update(bill);
        // update order status
        order.setStatus(OrderStatusType.PAID);
        orderDao.update(order);

        log.info("Bill with id [" + billId + "] successfully paid. Bill: " + bill);
        return true;
    }

    /**
     * return all Bills for User with id = userId,
     * if not one found - return empty List
     *
     * @param userId determine id of User who must pay Bill
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Bill> getAllForUserId(Serializable userId) {
        return billDao.getAllForUserId(userId);
    }

    /**
     * return all Bills for User with id = userId and BillStatusType = billStatus,
     * if not one found - return empty List
     *
     * @param billStatus determine Status of Bill
     * @param userId determine id of User who must pay Bill
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Bill> getAllForStatusAndUser(BillStatusType billStatus, Serializable userId) {
        return billDao.getAllForStatusAndUser(billStatus, userId);
    }

    /**
     * return all Bills for Hotel with id = hotelId and BillStatusType = billStatus,
     * if not one found - return empty List
     *
     * @param billStatus determine Status of Bill
     * @param hotelId determine id of Hotel that billed the bill
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Bill> getAllForStatusAndHotel(BillStatusType billStatus, Serializable hotelId) {
        return billDao.getAllForStatusAndHotel(billStatus, hotelId);
    }
}
