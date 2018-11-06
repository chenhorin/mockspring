package top.hrfeat.demo.mvc.action;

import top.hrfeat.demo.service.IDemoService;
import top.hrfeat.mvcframework.annotation.HRAutowired;
import top.hrfeat.mvcframework.annotation.HRController;
import top.hrfeat.mvcframework.annotation.HRRequestMapping;
import top.hrfeat.mvcframework.annotation.HRRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@HRController
@HRRequestMapping("/demo")
public class DemoAction {

    @HRAutowired
    private IDemoService iDemoService;

    @HRRequestMapping("/query.json")
    public void query(HttpServletRequest req, HttpServletResponse response,
                      @HRRequestParam("name") String name) {
        String res = iDemoService.get(name);
        try {
            response.getWriter().write(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @HRRequestMapping("/query.do")
    public void add(HttpServletRequest req, HttpServletResponse response,
                    @HRRequestParam("a") Integer a, @HRRequestParam("b") Integer b) {
        try {
            response.getWriter().write(a + "*" + b + " =" + a * b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @HRRequestMapping("/remove.do")
    public void remove(HttpServletRequest req, HttpServletResponse response,
                       @HRRequestParam("id") Integer id) {

    }
}
