package dao.impl;

import dao.IAdminDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojos.Admin;


@Repository
public class AdminDao extends BaseDao<Admin> implements IAdminDao {
    private static Logger log = Logger.getLogger(AdminDao.class);

    @Autowired
    public AdminDao() {
        super();
        clazz = Admin.class;
    }
}
