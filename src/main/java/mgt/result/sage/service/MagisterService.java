package mgt.result.sage.service;

import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Magister;
import mgt.result.sage.repository.MagisterRepository;
import mgt.result.sage.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MagisterService {
    @Autowired
    private MagisterRepository magisterRepo;

    @Autowired
    private Util util;


    public List<UserDetail> getAllStudents() {
        List<Magister> magisters = magisterRepo.findAll();
        return util.getUserDetails(magisters);
    }
}
