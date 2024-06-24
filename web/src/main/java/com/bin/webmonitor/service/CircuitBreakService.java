package com.bin.webmonitor.service;

import com.bin.webmonitor.component.SendCommand;
import com.bin.webmonitor.model.vo.CircuitBreakConfigVo;
import com.bin.webmonitor.model.vo.CircuitBreakEventVo;
import com.bin.webmonitor.model.vo.CircuitBreakMonitorVo;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CircuitBreakConfigDao;
import com.bin.webmonitor.repository.dao.CircuitBreakEventDao;
import com.bin.webmonitor.repository.dao.CircuitBreakMonitorDao;
import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import com.bin.webmonitor.repository.domain.CircuitBreakEvent;
import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CircuitBreakService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CircuitBreakConfigDao circuitBreakConfigDao;

    @Autowired
    private CircuitBreakMonitorDao circuitBreakMonitorDao;

    @Autowired
    private CircuitBreakEventDao circuitBreakEventDao;

    @Autowired
    private LocalService localService;

    @Autowired
    private LocalFunction localFunction;

    @Autowired
    private SendCommand sendCommand;

    public Map<Integer, Map<Integer, CircuitBreakConfig>> getCircuitBreakConfigs(int cid, Integer sid,
                                                                                 Integer fid) {
        Map<Integer, Map<Integer, CircuitBreakConfig>> res = new HashMap<>();

        List<CircuitBreakConfig> CircuitBreakConfigs =
                circuitBreakConfigDao.queryCircuitBreakConfigs(cid, sid, fid);

        for (CircuitBreakConfig CircuitBreakConfig : CircuitBreakConfigs) {
            Map<Integer, CircuitBreakConfig> fid2ConfigMap = res.get(CircuitBreakConfig.getSid());
            if (Objects.isNull(fid2ConfigMap)) {
                fid2ConfigMap = new HashMap<>();
                res.put(CircuitBreakConfig.getSid(), fid2ConfigMap);
            }

            fid2ConfigMap.put(CircuitBreakConfig.getFid(), CircuitBreakConfig);
        }

        return res;
    }

    
    public List<CircuitBreakMonitorVo> getCircuitBreakMonitors(int cid, Integer sid, Integer fid,
                                                               Pageable pageable) {
        List<CircuitBreakMonitor> CircuitBreakMonitors =
                circuitBreakMonitorDao.queryCircuitBreakMonitors(cid, sid, fid, pageable);

        return CircuitBreakMonitors.stream().map(po -> monitorPo2Vo(po)).collect(Collectors.toList());
    }

    
    public int getTotalCircuitBreakMonitorCount(int cid, Integer sid, Integer fid) {
        return circuitBreakMonitorDao.queryTotalCount(cid, sid, fid);
    }

    
    public List<CircuitBreakEventVo> getCircuitBreakEvents(int cid, Integer sid, Integer fid,
                                                           String ip, Pageable pageable) {
        List<CircuitBreakEvent> CircuitBreakEvents =
                circuitBreakEventDao.queryCircuitBreakEvents(cid, sid, fid, ip, pageable);

        return CircuitBreakEvents.stream().map(po -> eventPo2Vo(po)).collect(Collectors.toList());
    }

    
    public int getTotalCircuitBreakEventCount(int cid, Integer sid, Integer fid,
                                              String ip) {
        return circuitBreakEventDao.queryTotalCount(cid, sid, fid, ip);
    }

    
    public boolean createCircuitBreakConfig(CircuitBreakConfigVo circuitBreakConfigVo) {
        // 1.transform
        CircuitBreakConfig CircuitBreakConfig = new CircuitBreakConfig();
        BeanUtils.copyProperties(circuitBreakConfigVo, CircuitBreakConfig);

        // 2.入库
        int count = circuitBreakConfigDao.save(CircuitBreakConfig);

        // 3.向client发送配置变更通知
        sendCommand.sendCircuitBreakConfigChangeCommand(CircuitBreakConfig.getCid(), CircuitBreakConfig.getSid(),
                CircuitBreakConfig.getFid());

        logger.info("op=end_create,circuitBreakConfigVo={},CircuitBreakConfig={}，count={}", circuitBreakConfigVo,
                CircuitBreakConfig, count);
        return count > 0;
    }

    
    public boolean updateCircuitBreakConfigById(int id, Boolean enabled, Boolean forceOpened,
                                                Boolean forceClosed, Integer slideWindowInSeconds, Integer requestVolumeThreshold,
                                                Integer errorThresholdPercentage, Integer sleepWindowInMilliseconds) {
        // 1.入库
        int count = circuitBreakConfigDao.updateById(id, enabled, forceOpened, forceClosed, slideWindowInSeconds,
                requestVolumeThreshold, errorThresholdPercentage, sleepWindowInMilliseconds);

        // 2.向client发送变更通知
        CircuitBreakConfig CircuitBreakConfig = circuitBreakConfigDao.queryCircuitBreakConfigById(id);
        sendCommand.sendCircuitBreakConfigChangeCommand(CircuitBreakConfig.getCid(), CircuitBreakConfig.getSid(),
                CircuitBreakConfig.getFid());

        logger.info(
                "op=end_updateById,id={},enabled={},forceOpened={},forceClosed={},"
                        + "slideWindowInSeconds={},requestVolumeThreshold={},errorThresholdPercentage={},"
                        + "sleepWindowInMilliseconds={},count={}",
                id, enabled, forceOpened, forceClosed, slideWindowInSeconds, requestVolumeThreshold,
                errorThresholdPercentage, sleepWindowInMilliseconds, count);
        return count > 0;
    }

    
    public boolean deleteCircuitBreakConfigById(int id) {
        CircuitBreakConfig CircuitBreakConfig = circuitBreakConfigDao.queryCircuitBreakConfigById(id);

        // 1.库里删除
        circuitBreakConfigDao.deleteById(id);

        // 2.发送变更通知
        sendCommand.sendCircuitBreakConfigChangeCommand(CircuitBreakConfig.getCid(), CircuitBreakConfig.getSid(),
                CircuitBreakConfig.getFid());

        return true;
    }

    private CircuitBreakMonitorVo monitorPo2Vo(CircuitBreakMonitor CircuitBreakMonitor) {
        CircuitBreakMonitorVo circuitBreakMonitorVo = new CircuitBreakMonitorVo();

        BeanUtils.copyProperties(CircuitBreakMonitor, circuitBreakMonitorVo);
        circuitBreakMonitorVo.setService(localService.getById(CircuitBreakMonitor.getSid()).getServiceName());
        circuitBreakMonitorVo.setMethod(localFunction.function(CircuitBreakMonitor.getFid()));

        return circuitBreakMonitorVo;
    }

    private CircuitBreakEventVo eventPo2Vo(CircuitBreakEvent CircuitBreakEvent) {
        CircuitBreakEventVo circuitBreakEventVo = new CircuitBreakEventVo();

        BeanUtils.copyProperties(CircuitBreakEvent, circuitBreakEventVo);
        circuitBreakEventVo.setService(localService.getById(CircuitBreakEvent.getSid()).getServiceName());
        circuitBreakEventVo.setMethod(localFunction.function(CircuitBreakEvent.getFid()));

        return circuitBreakEventVo;
    }
}
