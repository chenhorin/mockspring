package top.hrfeat.demo.service;

import top.hrfeat.mvcframework.annotation.HRService;

import javax.naming.Name;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@HRService
public interface IDemoService {
    String get(String Name);
}
