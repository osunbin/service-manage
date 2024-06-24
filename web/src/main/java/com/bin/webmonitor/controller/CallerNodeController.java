package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.model.vo.ServiceNodeVO;
import com.bin.webmonitor.repository.domain.ClientConfig;
import com.bin.webmonitor.service.CallerNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caller/node")
public class CallerNodeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CallerNodeService callerNodeService;

    @RequestMapping("/list")
    public DataGrid<ServiceNodeVO> list( int cid) {
        logger.info("op=start_list,cid={}",  cid);

        return callerNodeService.getNodes(cid);
    }

    @RequestMapping("/getClientConfig")
    public ApiResult<ClientConfig> getClientConfig(int cid, String ip) {
        logger.info("op=start_getClientConfig,cid={},ip={}", cid, ip);
        ClientConfig clientconfig = callerNodeService.getClientConfig(cid, ip);
        logger.info("op=end_getClientConfig,clientconfig={}", clientconfig);
        return new ApiResult<ClientConfig>().setCode(Constants.SUCCESS).setResult(clientconfig);
    }
}
