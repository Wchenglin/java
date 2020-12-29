package com.lagou.edu.factory;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.lagou.edu.utils.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fasterxml.jackson.databind.util.ClassUtil.*;

/**
 * @author 应癫
 *
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    private String packageName;
    ConcurrentHashMap<String, Object> initBean = null;

    public BeanFactory(String packageName) {
        this.packageName = packageName;
    }

    // 使用beanID查找对象
    public Object getBean(String beanId) throws Exception {
        // 1.使用反射机制获取该包下所有的类已经存在bean的注解类
        List<Class> listClassesAnnotation = findClassExisService();
        if (listClassesAnnotation == null || listClassesAnnotation.isEmpty()) {
            throw new Exception("没有需要初始化的bean");
        }
        // 2.使用Java反射机制初始化对象
        initBean = initBean(listClassesAnnotation);
        if (initBean == null || initBean.isEmpty()) {
            throw new Exception("初始化bean为空!");
        }
        // 3.使用beanID查找查找对应bean对象
        Object object = initBean.get(beanId);
        // 4.使用反射读取类的属性,赋值信息
        attriAssign(object);
        return object;
    }

    // 使用反射读取类的属性,赋值信息         @resource实现
    public void attriAssign(Object object) throws IllegalArgumentException, IllegalAccessException {
        // 1.获取类的属性是否存在 获取bean注解
        Class<? extends Object> classInfo = object.getClass();
        Field[] declaredFields = classInfo.getDeclaredFields();
        for (Field field : declaredFields) {
            // 属性名称
            String name = field.getName();
            // 2.使用属性名称查找bean容器赋值
            Object bean = initBean.get(name);
            if (bean != null) {
                // 私有访问允许访问
                field.setAccessible(true);
                // 给属性赋值
                field.set(object, bean);
                continue;
            }
        }

    }

    // 使用反射机制获取该包下所有的类已经存在bean的注解类
    public List<Class> findClassExisService() throws Exception {
        // 1.使用反射机制获取该包下所有的类
        if (StringUtils.isEmpty(packageName)) {
            throw new Exception("扫包地址不能为空!");
        }
        // 2.使用反射技术获取当前包下所有的类
        List<Class<?>> classesByPackageName = ClassUtils.getClasses(packageName);
        // 3.存放类上有bean注入注解
        List<Class> exisClassesAnnotation = new ArrayList<Class>();
        // 4.判断该类上属否存在注解
        for (Class classInfo : classesByPackageName) {
            exisClassesAnnotation.add(classInfo);
        }
        return exisClassesAnnotation;
    }

    // 初始化bean对象
    public ConcurrentHashMap<String, Object> initBean(List<Class> listClassesAnnotation)
            throws InstantiationException, IllegalAccessException {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<String, Object>();
        for (Class classInfo : listClassesAnnotation) {
            // 初始化对象
            Object newInstance = classInfo.newInstance();
            // 获取父类名称
            String beanId = toLowerCaseFirstOne(classInfo.getSimpleName());
            concurrentHashMap.put(beanId, newInstance);
        }
        return concurrentHashMap;
    }

    // 首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
//    public static  Object getBean(String id) {
//        return map.get(id);
//    }


}
