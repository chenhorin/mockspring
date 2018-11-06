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

        System.out.println("-------------------------------mvc framework has init");
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
                baseUrl = mapping.value().replaceAll("/+", "/");
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(HRRequestMapping.class)) {
                    continue;
                }
                HRRequestMapping requestMapping = method.getAnnotation(HRRequestMapping.class);
                System.out.println("baseUrl :  " + baseUrl);
                System.out.println("requestMapping.value() : " + requestMapping.value());
                String url = (baseUrl + requestMapping.value().replaceAll("/+", "/"));
                handleMapping.put(url, method);
                System.out.println("mapping:  " + url + "   :   " + method);

            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {

                if (!field.isAnnotationPresent(HRAutowired.class)) {
                    continue;
                }
                HRAutowired autowired = field.getAnnotation(HRAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = lowerFirstCase(field.getType().getSimpleName());
                    System.out.println("beanName......." + beanName);
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
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
                    String beanName = lowerFirstCase(clazz.getSimpleName());
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
        in = this.getClass().getClassLoader().getResourceAsStream(initParameter);
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
        this.doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception , Detail:\r\n" + Arrays.toString(e.getStackTrace())
                    .replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (this.handleMapping.isEmpty()) {
            return;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        System.out.println("context" + contextPath);
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        System.out.println("我是url" + url);

        for (Map.Entry<String, Method> stringMethodEntry : handleMapping.entrySet()) {
            System.out.println(" handleMapping ：" + stringMethodEntry.getKey() + " : " + stringMethodEntry.getValue());
        }
        if (!this.handleMapping.containsKey(url)) {
            resp.getWriter().write("404 , not found");
            return;
        }
        Map<String, String[]> parameterMap = req.getParameterMap();

        /*Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            System.out.println("key参数类型"+stringEntry.getKey());
            System.out.print("value :");
            for (String s : stringEntry.getValue()) {
                System.out.println(s);
            }
        }*/
        Method method = this.handleMapping.get(url);
        System.out.println("被调用的method" + method);

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            System.out.println("我是入参的参数类型" + parameterType.getName());
        }

        Object[] paramValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];

            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
                continue;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
                continue;
            } else if (parameterType == String.class) {
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue())
                            .replaceAll("\\[|\\]", "")
                            .replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(this.ioc.get(beanName), paramValues);
    }
}
