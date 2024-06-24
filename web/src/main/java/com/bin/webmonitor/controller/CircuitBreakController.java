package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.model.vo.CircuitBreakConfigVo;
import com.bin.webmonitor.model.vo.CircuitBreakEventVo;
import com.bin.webmonitor.model.vo.CircuitBreakMonitorVo;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import com.bin.webmonitor.service.CircuitBreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bin.webmonitor.common.BaseError.PARAM_WRONG;

@RestController
@RequestMapping("/caller/circuitBreak")
public class CircuitBreakController extends BaseController {


    @Autowired
    private CircuitBreakService circuitBreakService;
    @Autowired
    private LocalService localService;
    @Autowired
    private LocalFunction localFunction;

    /**
     * 获取熔断配置
     */
    @RequestMapping(value = "/getCircuitBreakConfigs", method = RequestMethod.GET)
    public ApiResult<Map<String, Object>> getCircuitBreakConfigs(@RequestParam("cid") int cid,
                                                                 @RequestParam(value = "sid", required = false) Integer sid,
                                                                 @RequestParam(value = "fid", required = false) Integer fid) {
        Map<Integer, Map<Integer, CircuitBreakConfig>> circuitBreakConfigMap =
                circuitBreakService.getCircuitBreakConfigs(cid, sid, fid);

        Map<Integer, String> serviceMap = localService.getServiceNamesByIds(circuitBreakConfigMap.keySet());

        List<Integer> fids = new ArrayList<>();
        circuitBreakConfigMap.values().forEach(fid2ConfigMap -> fids.addAll(fid2ConfigMap.keySet()));
        Map<Integer, String> serviceFunctionMap = localFunction.getNamesByIds(fids);

        Map<String, Object> res = new HashMap<>();
        res.put("circuitBreakConfigMap", circuitBreakConfigMap);
        res.put("serviceMap", serviceMap);
        res.put("serviceFunctionMap", serviceFunctionMap);

        return okJson(res);
    }

    /**
     * 获取熔断监控数据
     */
    @RequestMapping(value = "/getCircuitBreakMonitors", method = RequestMethod.GET)
    public DataGrid<CircuitBreakMonitorVo> getCircuitBreakMonitors(@RequestParam("cid") int cid,
                                                                   @RequestParam(value = "sid", required = false) Integer sid,
                                                                   @RequestParam(value = "fid", required = false) Integer fid, Pageable pageable) {
        List<CircuitBreakMonitorVo> circuitBreakMonitorVos =
                circuitBreakService.getCircuitBreakMonitors(cid, sid, fid, pageable);

        int totalCount = circuitBreakService.getTotalCircuitBreakMonitorCount(cid, sid, fid);

        return new DataGrid.DataGridBuilder<CircuitBreakMonitorVo>().setCurrent(pageable.getPageNumber()).setRowCount(pageable.getPageSize())
                .setTotal(totalCount).setRows(circuitBreakMonitorVos).createDataGrid();

    }

    /**
     * 获取熔断事件
     */
    @RequestMapping(value = "/getCircuitBreakEvents", method = RequestMethod.GET)
    public DataGrid<CircuitBreakEventVo> getCircuitBreakEvents(@RequestParam("cid") int cid,
                                                               @RequestParam(value = "sid", required = false) Integer sid,
                                                               @RequestParam(value = "fid", required = false) Integer fid,
                                                               @RequestParam(value = "ip", required = false) String ip, Pageable pageable) {
        List<CircuitBreakEventVo> circuitBreakEventVos =
                circuitBreakService.getCircuitBreakEvents(cid, sid, fid, ip, pageable);

        int totalCount = circuitBreakService.getTotalCircuitBreakEventCount(cid, sid, fid, ip);

        return new DataGrid.DataGridBuilder<CircuitBreakEventVo>().setCurrent(pageable.getPageNumber()).setRowCount(pageable.getPageSize())
                .setTotal(totalCount).setRows(circuitBreakEventVos).createDataGrid();
    }

    /**
     * 创建熔断配置
     */
    @RequestMapping(value = "createCircuitBreakConfigs", method = {RequestMethod.POST, RequestMethod.GET})
    public ApiResult<Boolean> createCircuitBreakConfigs(CircuitBreakConfigVo circuitBreakConfigVo) {



        if (circuitBreakConfigVo.isForceClosed() && circuitBreakConfigVo.isForceOpened()) {
            return errorJson(PARAM_WRONG.code(), "不能同时强制关闭和强制打开！！！");
        }

        boolean res = circuitBreakService.createCircuitBreakConfig(circuitBreakConfigVo);
        log.info("op=end_create,circuitBreakConfigVo={},res={}",  circuitBreakConfigVo, res);
        return okJson(res);
    }

    /**
     * 修改熔断配置
     */
    @RequestMapping(value = "updateCircuitBreakConfigById", method = RequestMethod.GET)
    public ApiResult<Boolean> updateCircuitBreakConfigById(@RequestParam("enabled") int id,
                                                           @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                           @RequestParam(value = "forceOpened", required = false) Boolean forceOpened,
                                                           @RequestParam(value = "forceClosed", required = false) Boolean forceClosed,
                                                           @RequestParam(value = "slideWindowInSeconds", required = false) Integer slideWindowInSeconds,
                                                           @RequestParam(value = "requestVolumeThreshold", required = false) Integer requestVolumeThreshold,
                                                           @RequestParam(value = "errorThresholdPercentage", required = false) Integer errorThresholdPercentage,
                                                           @RequestParam(value = "sleepWindowInMilliseconds", required = false) Integer sleepWindowInMilliseconds) {
        boolean res = circuitBreakService.updateCircuitBreakConfigById(id, enabled, forceOpened, forceClosed,
                slideWindowInSeconds, requestVolumeThreshold, errorThresholdPercentage, sleepWindowInMilliseconds);

        log.info("op=end_updateById,id={},enabled={},forceOpened={},forceClosed={},"
                        + "slideWindowInSeconds={},requestVolumeThreshold={},errorThresholdPercentage={},sleepWindowInMilliseconds={},res={}",
                 id, enabled, forceOpened, forceClosed, slideWindowInSeconds, requestVolumeThreshold,
                errorThresholdPercentage, sleepWindowInMilliseconds, res);
        return okJson(res);
    }

    /**
     * 删除熔断配置
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteCircuitBreakConfig", method = RequestMethod.GET)
    public ApiResult<Boolean> deleteCircuitBreakConfig(@RequestParam("id") int id) {
        boolean res = circuitBreakService.deleteCircuitBreakConfigById(id);
        log.info("op=end_deleteCircuitBreakConfig,id={},res={}",  id, res);

        return okJson(res);
    }
}
