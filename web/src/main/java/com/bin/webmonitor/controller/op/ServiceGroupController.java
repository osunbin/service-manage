package com.bin.webmonitor.controller.op;

import java.util.List;
import java.util.Objects;

import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.environment.EnvironmentEnum;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.bin.webmonitor.common.Constants.DEFAULT_GROUP_NAME;

@RestController("opServiceGroupController")
@RequestMapping("/op/service/group")
public class ServiceGroupController extends BaseController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private GroupDao groupDao;
    
    @Autowired
    private CallerUsageDao callerUsageDao;
    
    @Autowired
    private EnvManager envManager;
    
    @RequestMapping("/changeAllDefaultGroup")
    @ResponseBody
    public ApiResult<Boolean> changeAllDefaultGroup() {
        logger.info("op=start_changeAllDefaultGroup");
        if (envManager.getCurrentEnvironment() != EnvironmentEnum.TEST) {
            return errorJson("非测试环境禁止调用！");
        }
        List<CallerUseage> callerUsages = callerUsageDao.selectAll();
        for (CallerUseage callerUsage : callerUsages) {
            Group checkGroup = groupDao.selectBySidAndGroupName(callerUsage.getSid(), DEFAULT_GROUP_NAME);
            if (Objects.isNull(checkGroup)) {
                Group insertGroup = new Group().setSid(callerUsage.getSid()).setGroupName(DEFAULT_GROUP_NAME).setStatus(0);
                int count = groupDao.insert(insertGroup);
                if (count != 1) {
                    logger.warn("[warn_add_default_group_failed]op=changeAllDefaultGroup,insertGroup={}", insertGroup);
                    continue;
                }
            }
            Group group = groupDao.selectBySidAndGroupName(callerUsage.getSid(), DEFAULT_GROUP_NAME);
            callerUsageDao.updateGroup(callerUsage.getSid(), callerUsage.getCid(), group.getId());
        }
        return okJson();
    }
    
}
