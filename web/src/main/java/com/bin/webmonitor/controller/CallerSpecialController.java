package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.model.dto.CallerSpecialDto;
import com.bin.webmonitor.model.vo.Attributes;
import com.bin.webmonitor.model.vo.CallerSpecialVo;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CallerSpecialService;
import com.bin.webmonitor.service.CallerUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.bin.webmonitor.common.BaseError.ID_NOT_EXIST;
import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/abtest")
public class CallerSpecialController  extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CallerSpecialController.class);

    @Autowired
    private CallerSpecialService callerSpecialService;
    @Autowired
    private CallerUsageService callerUsageService;

    @Autowired
    private CallerDao callerDao;

    @Autowired
    private LocalService localService;

    /**
     * 调用方灰度设置
     */
    @RequestMapping(value = "/auth/create", method = RequestMethod.POST)
    public ApiResult<Integer> create( CallerSpecialDto dto) {

        delCipsCommas(dto);

        logger.info(" create CallerSpecial req dto:{} ",  dto.toString());
        if (hasIpDuplicate(dto)) {
            return errorJson("调用方ip不能重复");
        }
        if (callerSpecialService.hasDuplicateCallerIp(dto, false)) {
            return errorJson("存在相同的调用方Ip");
        }
        checkArgument(callerUsageService.isExist(dto.getCid(), dto.getSid()), "请在创建调用关系后,再新增灰度配置");


        ServiceInstance byId = localService.getById(dto.getSid());
        dto.setServiceName(byId.getServiceName());
        Caller caller = callerDao.selectByPrimaryKey(dto.getCid());
        dto.setCallerName(caller.getCallerName());


        CallerSpecialAttr po = new CallerSpecialAttr();
        BeanUtils.copyProperties(dto, po);

        Attributes attributes = new Attributes(dto.getSerialize(), dto.getSips());
        po.setAttrJson(attributes.toString());

        callerSpecialService.create(po);

        logger.info("createCallerSpecial");
        return okJson(1);
    }

    private void delCipsCommas(CallerSpecialDto dto) {
        List<String> cipList = DelimiterHelper.splitToList(dto.getCips());
        List<String> resultList = new ArrayList<>();
        for (String cip : cipList) {
            if (DelimiterHelper.notEmpty(cip)) {
                resultList.add(cip);
            }
        }
        String newCips = DelimiterHelper.join(resultList);
        dto.setCips(newCips);
    }

    /**
     * 调用方灰度设置-修改
     */
    @RequestMapping(value = "/auth/update", method = RequestMethod.POST)
    public ApiResult<Integer> update( CallerSpecialDto dto) {

        checkArgument(null != dto.getId(), "id不能为空");
        delCipsCommas(dto);
        if (hasIpDuplicate(dto)) {
            return errorJson("调用方ip不能重复");
        }
        if (callerSpecialService.hasDuplicateCallerIp(dto, true)) {
            return errorJson("存在相同的调用方Ip");
        }

        logger.info(" update CallerSpecial req dto:{} ", dto.toString());


        CallerSpecialAttr po = new CallerSpecialAttr();
        BeanUtils.copyProperties(dto, po);

        Attributes attributes = new Attributes(dto.getSerialize(), dto.getSips());
        po.setAttrJson(attributes.toString());

        callerSpecialService.updateByPrimaryKeySelective(po);


        logger.info("createCallerSpecial");
        return okJson(1);
    }

    /**
     * 删除调用方灰度设置.
     */
    @RequestMapping(value = "/auth/del", method = RequestMethod.DELETE)
    public ApiResult<String> deleteById(@RequestParam("id") Integer id) {

        logger.info(" update CallerSpecial req id:{} ",  id);
        Integer result = callerSpecialService.deleteByPrimaryKey(id);
        if (result == 1) {
            return okJson();
        }
        return errorJson(ID_NOT_EXIST.code(), ID_NOT_EXIST.getMessage());
    }

    /**
     * 查看调用方灰度设置详情.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult<CallerSpecialVo> getById(@PathVariable("id") Integer id) {
        logger.info("getCallerSpecialById req:{}", id);
        CallerSpecialVo vo = callerSpecialService.findByPrimaryKey(id);
        logger.info("getCallerSpecialById resp:{}", vo.toString());
        return okJson(vo);
    }

    private boolean hasIpDuplicate(CallerSpecialDto dto) {
        String cips = dto.getCips();
        List<String> cipList = DelimiterHelper.splitToList(cips);
        return new HashSet<>(cipList).size() != cipList.size();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult<String> updateByIdSelective(@PathVariable("id") Integer id,
                                                 @RequestBody CallerSpecialAttr callerSpecialAttr) {
        callerSpecialAttr.setId(id);
        Integer result = callerSpecialService.updateByPrimaryKeySelective(callerSpecialAttr);
        if (result == 1) {
            return okJson();
        }
        return errorJson(ID_NOT_EXIST.code(), ID_NOT_EXIST.getMessage());
    }

    /**
     * 灰度配置列表.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DataGrid<CallerSpecialVo> listPos(@PageableDefault Pageable pageable, CallerSpecialAttr po) {
        List<CallerSpecialVo> vos = callerSpecialService.selectPage(po, pageable);
        int total = callerSpecialService.selectCount(po);
        return new DataGrid.DataGridBuilder<CallerSpecialVo>()
                .setCurrent(pageable.getPageNumber())
                .setRowCount(pageable.getPageSize())
                .setTotal(total)
                .setRows(vos)
                .createDataGrid();
    }

}
