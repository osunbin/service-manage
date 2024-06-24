package com.bin.webmonitor.controller;

import com.bin.webmonitor.environment.EnvManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class IndexControler {
    @Resource
    private EnvManager envManager;

    @RequestMapping("/")
    public String serviceIndex() {
        return envManager.getCurrentEnvironment().getDesc();
    }

}