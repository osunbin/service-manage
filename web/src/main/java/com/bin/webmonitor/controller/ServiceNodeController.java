package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.vo.ServiceNodeConfigVo;
import com.bin.webmonitor.model.vo.ServiceNodeVO;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.ServiceConfig;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.OperateRecordService;
import com.bin.webmonitor.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/node")
public class ServiceNodeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ServiceNodeService serviceNodeService;

    @Autowired
    private LocalService localService;

    @Autowired
    private OperateRecordService operateRecordService;


    @RequestMapping("/delete")
    public ApiResult<Boolean> delete(ServiceInstance service, String ip) {
        operateRecordService.addOperateRecord(null, service.getId(), OperateRecordType.SERVICE_NODE_DELETE, "删除节点:" + ip);
        return serviceNodeService.delete(service, ip);
    }

    @RequestMapping("/goNodes")
    public ApiResult<ServiceInstance> goNodes(ModelMap modelMap, int id) {
        ServiceInstance service = localService.getById(id);
        modelMap.addAttribute("service", service);
        logger.info("service={}",service.getServiceName());
        return new ApiResult<ServiceInstance>().setResult(service);
    }

    @RequestMapping("/list")
    public DataGrid<ServiceNodeVO> list(ServiceInstance service) {
        return serviceNodeService.list( service);
    }

    @RequestMapping("/onOff")
    public ApiResult<Boolean> onOff(ServiceInstance service, String ip, boolean on) {
        return serviceNodeService.onOff(service, ip, on);
    }

    @RequestMapping("/getCallerNoServiceGroupNodeList")
    public String getCallerNoGroupNodeList() {
        List<String> callerNoGroupNodeList = serviceNodeService.getCallerNoServiceGroupNodeList();
        return DelimiterHelper.COMMAS_BR.join(callerNoGroupNodeList);
    }

    /**
     * 更新节点配置信息，如权重等
     *
     * @param serviceNodeConfigVo
     * @return
     */
    @RequestMapping("/updateServiceNodeConfig")
    public ApiResult<Boolean> updateServiceNodeConfig( ServiceNodeConfigVo serviceNodeConfigVo) {
        logger.info("op=start_updateServiceNodeConfig,serviceNodeConfigVo={}", serviceNodeConfigVo);
        try {
            if (serviceNodeConfigVo.getNewWeight() < 0 || serviceNodeConfigVo.getNewWeight() > 1000) {
                logger.error("[ERROR_updateServiceNodeConfig_weight]serviceNodeConfigVo={}", serviceNodeConfigVo);
                return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS)
                        .setMessage("非法权重值[" + serviceNodeConfigVo.getNewWeight() + "]. 必须在[0,1000]之间");
            }
            ApiResult<Boolean> res=serviceNodeService.updateServiceNodeConfig( serviceNodeConfigVo);
            return res;
        }catch(Exception e){
            logger.error("[error_updateServiceNodeConfig]serviceNodeConfigVo={},",serviceNodeConfigVo,e);
            return new ApiResult<Boolean>().setCode(Constants.SERVER_ERROR).setMessage("平台内部出现错误");
        }
    }

    @RequestMapping("/getServiceConfig")
    public ApiResult<ServiceConfig> getServiceConfig(ServiceInstance service, String ip) {
        logger.info("op=start_getServiceConfig,service={},ip={}", service, ip);
        ServiceConfig serviceConfig = serviceNodeService.getServiceConfig(service.getServiceName(), ip);
        logger.info("op=end_getServiceConfig,serviceConfig={}", serviceConfig);
        return new ApiResult<ServiceConfig>().setCode(Constants.SUCCESS).setResult(serviceConfig);
    }
}
