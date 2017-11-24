package controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.Bill;
import pojos.User;
import pojos.enums.BillStatusType;
import services.IBillService;
import vo.BillVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Controller
@RequestMapping("/bills_user")
public class BillPageUserController {
    private static Logger log = Logger.getLogger(BillPageUserController.class);

    private static final String BILLS_USER_MAIN = "bills_user/main";
    private final BillStatusType DEFAULT_BILL_STATUS = BillStatusType.UNPAID;
    private final Long DEFAULT_ID = -1L;

    private IBillService billService;

    @Autowired
    public BillPageUserController(IBillService billService) {
        this.billService = billService;
    }

    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String mainPage(ModelMap model, HttpServletRequest request) {
        // del after debug
        System.out.println("-> SecurityContextHolder.getContext().getAuthentication():" + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("request.getQueryString(): " + request.getQueryString());


        // check valid bill status, if invalid then  BillStatusType.UNPAID
        String billStatusStr = request.getParameter("billStatus") != null
                ? (String) request.getParameter("billStatus")
                : null;
        // del after debug
        System.out.println("->: BillPageUserController.mainPage. billStatusStr: " + billStatusStr);

        BillStatusType billStatus;
        try {
            billStatus = BillStatusType.valueOf(billStatusStr);
        } catch (NullPointerException | IllegalArgumentException e) {
            billStatus = DEFAULT_BILL_STATUS;
        }
        // del after debug
        System.out.println("->: BillPageUserController.mainPage. set billStatus= " + billStatus);

        fillModel(model, billStatus, getCurrentUserId(request.getSession()));

        return BILLS_USER_MAIN;
    }

    @Transactional
    @RequestMapping(value = "/payBill", method = {RequestMethod.GET, RequestMethod.POST})
    public String payBillByUser(ModelMap model, BillVo billVo, HttpSession session) {
        // del after debug
        System.out.println("->: BillPageUserController.payBillByUser. billVo: " + billVo);

        // check valid request param
        Long billId = billVo != null && billVo.getBillId() != null
                ? billVo.getBillId()
                : DEFAULT_ID;
        Long currentUserId = (Long) getCurrentUserId(session);

        Bill bill = billService.get(billId);

        boolean isBillPay = false;
        // try to pay bill
        if (bill != null && currentUserId.equals(bill.getUser().getId())  ) {
            isBillPay = billService.billPay(bill.getId());
        }

        // safe billId in request
        model.addAttribute("billId", billId);

        // save message in request if try pay NOT null Bill
        if ( ! billId.equals(DEFAULT_ID)) {
            if (isBillPay) {
                // success message in request
                model.addAttribute("billPaidMsg", "Paid bill with id= " + billId);
            } else {
                // error message in request
                model.addAttribute("billCanNotPayErrorMsg",
                        "Can't pay bill with id= " + billId);
            }
        }

        fillModel(model, DEFAULT_BILL_STATUS, currentUserId);

        return BILLS_USER_MAIN;
    }
    private void fillModel(ModelMap model, BillStatusType billStatus, Serializable userId) {
        List<Bill> billList = billService.getAllForStatusAndUser(billStatus, userId);

        populatePageTitle(model);
        model.addAttribute("billList", billList);
        model.addAttribute("billVo", new BillVo());
        model.addAttribute("billStatus", billStatus);
    }
    private void populatePageTitle(ModelMap model) {
        model.addAttribute("currentPageName", "bills_user");
        model.addAttribute("currentTitle", "page.bills.title");
    }

    private Serializable getCurrentUserId(HttpSession session) {
        // take current User from session to determine user id
        User user = session.getAttribute("user") != null
                ? (User) session.getAttribute("user")
                : null;

        return user != null ? user.getId() : DEFAULT_ID;
    }
}
