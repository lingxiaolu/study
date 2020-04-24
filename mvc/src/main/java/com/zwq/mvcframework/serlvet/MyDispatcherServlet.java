package com.zwq.mvcframework.serlvet;

import com.zwq.mvcframework.annotation.*;
import com.zwq.mvcframework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherServlet extends HttpServlet {
    // 读取配置文件
    private Properties properties = new Properties();
    // ioc容器（存放实例化对象）
    private Map<String, Object> beanMap = new HashMap<>();
    // 存储扫描到的类的全限定名
    private List<String> classNames = new ArrayList<>();

    // 管理url跟method的映射关系
//    private Map<String, Method> handlerMapping = new HashMap<>();
    private List<Handler> handlers = new ArrayList<>();




    @Override
    public void init(ServletConfig config) throws ServletException {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        // 1、  读取配置文件
        doLoadConfig(contextConfigLocation);
        // 2、  扫描包 扫描注解
        doScan(properties.getProperty("scanPackage"));
        // 3、ioc容器初始化以及bean的创建和依赖注入
        doInstance();
        // 依赖注入
        doAutowired();
        // 4、构造⼀个HandlerMapping处理器映射器，将配置好的url和Method建⽴映射关系
        initHandlerMapping();

        // 5、等待请求进来，处理请求

        System.out.println("mvc 初始化完成");

    }

    private void initHandlerMapping() {

        if (beanMap.isEmpty()){
            return;
        }

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if(!aClass.isAnnotationPresent(MyRequestMapping.class)){
                continue;
            }
            String uri = null;
            MyRequestMapping annotation = aClass.getAnnotation(MyRequestMapping.class);
            String typeUri = annotation.value();
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                // 对于方法上有MyRequestMapping注解的才进行处理
                if (!method.isAnnotationPresent(MyRequestMapping.class)){
                    continue;
                }

                MyRequestMapping annotation1 = method.getAnnotation(MyRequestMapping.class);
                String methodUri = annotation1.value();
                uri = typeUri + methodUri;
                Handler handler = new Handler(entry.getValue(),method,Pattern.compile(uri));
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter.getType()==HttpServletRequest.class || parameter.getType()==HttpServletResponse.class){
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), i);
                    }else {
                        handler.getParamIndexMapping().put(parameter.getName(), i);
                    }
                }
                handlers.add(handler);
            }

        }

    }

    private void doAutowired() {
        if (beanMap.isEmpty()){
            return;
        }
        try{
            for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                String key = entry.getKey();
                Field[] fields = beanMap.get(key).getClass().getFields();
                for (Field field : fields) {
                    if(!field.isAnnotationPresent(MyAutowired.class)){
                        continue;
                    }
                    MyAutowired annotation = field.getAnnotation(MyAutowired.class);
                    String beanName = annotation.value();
                    //  没有配置具体的bean id，那就需要根据当前字段类型注⼊（接⼝注入） IDemoService
                    if ("".equals(beanName.trim())){
                        beanName = field.getType().getName();
                    }
                    field.setAccessible(true);
                    field.set(entry.getValue(),beanMap.get(beanName));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doInstance() {

        if (classNames.isEmpty()){
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> aClass = Class.forName(className);
                // 区别controller service
                if (aClass.isAnnotationPresent(MyController.class)){
                    // controller的id此处不做过多处理，不取value了，就拿类的⾸字⺟⼩写作为id，保存到ioc中
                    String simpleName = aClass.getSimpleName();
                    String lowFirstSimpleName = lowFirst(simpleName);
                    beanMap.put(lowFirstSimpleName, aClass.newInstance());
                }else if (aClass.isAnnotationPresent(MyService.class)){
                    // service 则做两种情况判断，没写id就直接用类名首字母小写，写了的就直接存入
                    MyService annotation = aClass.getAnnotation(MyService.class);
                    String beanName = annotation.value();
                    if (!"".equals(beanName.trim())){
                        beanMap.put(beanName, aClass.newInstance());
                    }else{
                        beanMap.put(lowFirst(aClass.getSimpleName()),aClass.newInstance());
                    }
                    // service层往往是有接⼝的，⾯向接⼝开发，此时再以接⼝名为id，放⼊⼀份对象到ioc中，便于后期根据接⼝类型注⼊
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        beanMap.put(anInterface.getName(), aClass.newInstance());
                    }
                }else{
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private String lowFirst(String simpleName) {
        char[] chars = simpleName.toCharArray();
        if ('A'<=chars[0] && 'Z'>=chars[0]){
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    // scanPackage :com.zwq.demo  scanPackagePath----> 磁盘上的⽂件夹（File）
    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+scanPackage.replaceAll("\\.","/");
        File pack = new File(scanPackagePath);
        File[] files = pack.listFiles();
        for (File file : files) {
            if (file.isDirectory()){
                // 递归 //com.zwq.demo.controller
                doScan(scanPackage+"."+file.getName());
            }else if (file.getName().endsWith(".class")){
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }

        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            properties.load(resourceAsStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理请求：根据url，找到对应的Method方法，进行调用
        // 获取uri
//        String requestURI = req.getRequestURI();
//        Method method = handlerMapping.get(requestURI);// 获取到一个反射的方法
        // 反射调用，需要传入对象，需要传入参数，此处无法完成调用，没有把对象缓存起来，也没有参数！！！！改造initHandlerMapping();
//        method.invoke()
        // 根据uri获取到能够处理当前请求的hanlder（从handlermapping中（list））
        resp.setHeader("content-type", "text/html;charset=utf-8");
        Handler handler = getHandler(req);
        if (handler==null){
            resp.getWriter().write("404 not found");
            return;
        }

        if (handler.getController().getClass().isAnnotationPresent(MySecurity.class)){
            MySecurity annotation = handler.getController().getClass().getAnnotation(MySecurity.class);
            String[] value = annotation.value();
            String username = req.getParameter("username");
            List<String> strings = Arrays.asList(value);
            if (!strings.contains(username)){
                resp.getWriter().write("403 请求的资源不允许访问");
                return;
            }
        }
        // 参数绑定
        // 获取所有参数类型数组，这个数组的⻓度就是我们最后要传⼊的args数组的⻓度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        // 根据上述数组⻓度创建⼀个新的数组（参数数组，是要传⼊反射调⽤的）
        Object[] paraValues = new Object[parameterTypes.length];
        // 以下就是为了向参数数组中塞值，⽽且还得保证参数的顺序和⽅法中形参顺序⼀致
        Map<String, String[]> parameterMap = req.getParameterMap();
        // 遍历request中所有参数 （填充除了request，response之外的参数）
        for(Map.Entry<String,String[]> param: parameterMap.entrySet()) {
            // name=ls&name=hw name [ls,hw]
            String value = StringUtils.join(param.getValue(), ","); // 此时value为：ls,hw
            // 如果参数和⽅法中的参数匹配上了，填充数据

            if(!handler.getParamIndexMapping().containsKey(param.getKey()))
            {continue;}
            // ⽅法形参确实有该参数，找到它的索引位置，对应的把参数值放⼊paraValues
            Integer index =
                    handler.getParamIndexMapping().get(param.getKey());//name在第 2 个位置
            paraValues[index] = value; // 把前台传递过来的参数值填充到对应的位置去
        }
        int requestIndex =
                handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName(
                )); // 0
        paraValues[requestIndex] = req;
        int responseIndex =
                handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName
                        ()); // 1
        paraValues[responseIndex] = resp;
        // 最终调⽤handler的method属性
        try {

            handler.getMethod().invoke(handler.getController(),paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    private Handler getHandler(HttpServletRequest req) {
        if(handlers.isEmpty()){
            return null;
        }
        String requestURI = req.getRequestURI();
        for (Handler handler : handlers) {
            Matcher matcher = handler.getPattern().matcher(requestURI);
            if (!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }


}
