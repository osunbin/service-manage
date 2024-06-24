package com.bin.webmonitor.service;

import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.model.dto.CallerSpecialDto;
import com.bin.webmonitor.model.vo.Attributes;
import com.bin.webmonitor.model.vo.CallerSpecialVo;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.CallerSpecialDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bin.webmonitor.common.Constants.PAGE_MAX;

@Service
public class CallerSpecialService {

    @Autowired
    private CallerSpecialDao callerSpecialAttrDao;



    @Autowired
    private CallerDao callerDao;

    @Autowired
    private LocalService localService;



    public int create(CallerSpecialAttr callerSpecialAttr) {
        return callerSpecialAttrDao.insert(callerSpecialAttr);
    }



    public int deleteByPrimaryKey(Integer id) {
        return callerSpecialAttrDao.deleteByPrimaryKey(id);
    }


    public CallerSpecialVo findByPrimaryKey(Integer id) {
        CallerSpecialAttr po = callerSpecialAttrDao.selectByPrimaryKey(id);
        return po2Vo(po);
    }

    private CallerSpecialVo po2Vo(CallerSpecialAttr po) {
        Integer cid = po.getCid();
        Integer sid = po.getSid();
        Caller caller = callerDao.selectByPrimaryKey(cid);
        ServiceInstance service = localService.getById(sid);

        CallerSpecialVo vo = new CallerSpecialVo();
        BeanUtils.copyProperties(po, vo);
        vo.setCallerName(caller.getCallerName());
        vo.setServiceName(service.getServiceName());

        Attributes attributes = JsonHelper.fromJson(po.getAttrJson(), Attributes.class);
        vo.setSerialize(attributes.getSerialize());
        vo.setSips(attributes.getSips());
        vo.setCreateTime(TimeUtil.date2fullStr(po.getCreateTime()));
        return vo;
    }


    public int selectCount(CallerSpecialAttr callerSpecialAttr) {
        return callerSpecialAttrDao.selectCount(callerSpecialAttr);
    }


    public int updateByPrimaryKey(CallerSpecialAttr callerSpecialAttr) {
        return callerSpecialAttrDao.updateByPrimaryKey(callerSpecialAttr);
    }


    public int updateByPrimaryKeySelective(CallerSpecialAttr callerSpecialAttr) {
        return callerSpecialAttrDao.updateByPrimaryKeySelective(callerSpecialAttr);
    }


    public List<CallerSpecialVo> selectPage(CallerSpecialAttr callerSpecialAttr, Pageable pageable) {
        List<CallerSpecialAttr> callerSpecialAttrs = callerSpecialAttrDao.selectPage(callerSpecialAttr, pageable);
        return callerSpecialAttrs.stream().map(po -> po2Vo(po)).collect(Collectors.toList());
    }

    


    
    public boolean hasDuplicateCallerIp(CallerSpecialDto dto, boolean isUpdate) {
        Integer cid = dto.getCid();
        Integer sid = dto.getSid();
        String cips = dto.getCips();
        
        Set<String> nowIpSet = new HashSet<>(DelimiterHelper.splitToList(cips));
        
        CallerSpecialAttr po = new CallerSpecialAttr();
        po.setCid(cid);
        po.setSid(sid);
        List<CallerSpecialAttr> callerSpecialAttrs = callerSpecialAttrDao.selectPage(po, PAGE_MAX);
        for (CallerSpecialAttr item : callerSpecialAttrs) {
            String currentIps = item.getCips();
            Set<String> originIpSet = new HashSet<>(DelimiterHelper.splitToList(currentIps));
            Sets.SetView<String> intersection = Sets.intersection(nowIpSet, originIpSet);
            if (intersection.size() > 0 && !dto.getId().equals(item.getId())) {
                return true;
            }
        }
        return false;
    }
}
