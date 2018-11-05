package top.hrfeat.mvcframework.servlet;

import top.hrfeat.mvcframework.annotation.HRAutowired;
import top.hrfeat.mvcframework.annotation.HRController;
import top.hrfeat.mvcframework.annotation.HRRequestMapping;
import top.hrfeat.mvcframework.annotation.HRService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;


public class HRDispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LOCATION = "contextConfigLocation";

    private Properties p = new Properties();

    private List<String> classNames = new ArrayList<String>();

    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Map<String, Method> handleMapping = new HashMap<String, Method>();



    public HRDispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        doLoad(config.getInitParameter(LOCATION));

        String scanPackage = p.getProperty("scanPackage");
        doScanner(scanPackage);
        System.out.println(scanPackage);

        doInstance();

        doAutowired();

        initHandleMapping();

        System.out.println("mvc framework has init");
    }

    private void initHandleMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(HRController.class)) {
                continue;
            }

            String baseUrl = "";
//            获取controller的url配置
            if (clazz.isAnnotationPresent(HRRequestMapping.class)) {
                HRRequestMapping mapping = clazz.getAnnotation(HRRequestMapping.class);
                baseUrl = mapping.value();
            }

            Method[] methods = clazz.getMethods();
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getClass().getDeclaredFields();
            for (Field field : fields) {

                if (!field.isAnnotationPresent(HRAutowired.class)) {
                    continue;
                }
                HRAutowired autowired = field.getAnnotation(HRAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }
    }

    private void doInstance() {
        if (classNames.size() == 0) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(HRController.class)) {
                    String beanName = lowerFirstCase(className);
                    System.out.println(beanName);
                    ioc.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(HRService.class)) {
                    HRService service = clazz.getAnnotation(HRService.class);
                    String beanName = service.value();

                    if (!"".equals(beanName.trim())) {
                        ioc.put(beanName, clazz.newInstance());
                        continue;
                    }
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        ioc.put(anInterface.getName(), clazz.newInstance());
                    }
                } else {
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    首字母小写
    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                classNames.add(scanPackage + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doLoad(String initParameter) {
        InputStream in = null;
        in = this.getClass().getClassLoader().getResourceAsStream(LOCATION);
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
