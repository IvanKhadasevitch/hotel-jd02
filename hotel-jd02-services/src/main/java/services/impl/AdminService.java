package services.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Admin;
import services.IAdminService;

@Service
@Transactional(transactionManager = "txManager")
public class AdminService extends BaseService<Admin> implements IAdminService {
    private static Logger log = Logger.getLogger(AdminService.class);

    public AdminService() {
        super();
    }
}
