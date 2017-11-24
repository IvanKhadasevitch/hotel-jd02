package controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojos.Admin;
import pojos.Bill;
import pojos.enums.BillStatusType;
import services.IBillService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/bills_admin")
public class BillPageAdminController  {
    private static Logger log = Logger.getLogger(BillPageAdminController.class);

    private static final String BILLS_ADMIN_MAIN = "bills_admin/main";
    private final BillStatusType DEFAULT_BILL_STATUS = BillStatusType.UNPAID;

    private IBillService billService;

    @Autowired
    public BillPageAdminController(IBillService billService) {
        this.billService = billService;
    }

    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String mainPage(ModelMap model, HttpServletRequest request) {

        // check valid bill status, if invalid then  BillStatusType.UNPAID
        String billStatusStr = request.getParameter("billStatus") != null
                ? (String) request.getParameter("billStatus")
                : null;
        // del after debug
        System.out.println("->: BillPageAdminController.mainPage. billStatusStr: " + billStatusStr);

        BillStatusType billStatus;
        try {
            billStatus = BillStatusType.valueOf(billStatusStr);
        } catch (NullPointerException | IllegalArgumentException e) {
            billStatus = DEFAULT_BILL_STATUS;
        }
        // del after debug
        System.out.println("->: BillPageAdminController.mainPage. set billStatus= " + billStatus);

        fillModel(model, billStatus, getCurrentHotelId(request));

        return BILLS_ADMIN_MAIN;
    }

    private void fillModel(ModelMap model, BillStatusType billStatus, Serializable hotelId) {
        List<Bill> billList = billService.getAllForStatusAndHotel(billStatus, hotelId);
        Date currentDate = new Date((new java.util.Date()).getTime());

        // del after debug
        System.out.println("->: BillPageAdminController.fillModel. set billList size: " + billList.size());
        System.out.println("->: BillPageAdminController.fillModel. set currentDate: " + currentDate);


        // save in request: currentDate, billList, billStatus, currentPageName, currentTitle
        populatePageTitle(model);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("billList", billList);
        model.addAttribute("billStatus", billStatus);
    }
    private void populatePageTitle(ModelMap model) {
        model.addAttribute("currentPageName", "bills_admin");
        model.addAttribute("currentTitle", "page.bills.title");
    }

    private Serializable getCurrentHotelId(HttpServletRequest request) {
        // take current Admin from session to determine Hotel id
        Admin admin = request.getSession().getAttribute("admin") != null
                ? (Admin) request.getSession().getAttribute("admin")
                : null;

        final Long DEFAULT_ID = -1L;
        return (admin != null && admin.getHotel() != null)
                ? admin.getHotel().getId()
                : DEFAULT_ID;
    }
}
