package top.hrfeat.demo.service.impl;

import top.hrfeat.demo.service.IDemoService;
import top.hrfeat.mvcframework.annotation.HRService;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@HRService("iDemoService")
public class DemoServiceImpl implements IDemoService     {
    public String get(String name) {
        return name+":hello godfather";
    }
}
