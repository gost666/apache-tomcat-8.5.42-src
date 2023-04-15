/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Java;


/**
 * Utility class to read the bootstrap Catalina configuration.
 *
 * @author Remy Maucherat
 */
public class CatalinaProperties {

    private static final Log log = LogFactory.getLog(CatalinaProperties.class);

    private static Properties properties = null;


    static {
        loadProperties();
    }


    /**
     * @param name The property name
     * @return specified property value
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }


    /**
     * Load properties.
     */
    private static void loadProperties() {

        System.out.println("====>><<加载catalina.properties配置文件====");
        InputStream is = null;
        try {
            String configUrl = System.getProperty("catalina.config");
            if (configUrl != null) {
                is = (new URL(configUrl)).openStream();
            }
        } catch (Throwable t) {
            handleThrowable(t);
        }

        if (is == null) {
            try {
                //在 Add Tomcat VM Options参数中配置的路径:
                File home = new File(Bootstrap.getCatalinaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, "catalina.properties");
                is = new FileInputStream(propsFile);
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        if (is == null) {
            try {
                is = CatalinaProperties.class.getResourceAsStream
                    ("/org/apache/catalina/startup/catalina.properties");
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);

                /**
                 * 测试 Properties.load0(new LineReader(inStream)) 中的 new LineReader(inStream)参数
                 * 内部类创建对象的方式:
                 *  //方式一
                 *  OuterClass.InnerClass oi = new OuterClass().new InnerClass();
                 *  //方式二
                 *  OuterClass o = new OuterClass();
                 *  OuterClass.InnerClass i = o.new InnerClass();
                 *
                 *  内部类分类:静态内部类和非静态内部类(成员内部类,方法内部类,匿名内部类)
                 *  区别:
                 *                              静态内部类       非静态内部类
                 *  是否可以有静态成员变量             是               否
                 *  是否可以访问外部类的非静态变量      否               是
                 *  是否可以访问外部类的静态变量       是               是
                 *  创建是否依赖于外部类             否               是
                 *
                 *
                 * 内部类与外部类的关系:
                 *  a.对于非静态内部类,内部类的创建依赖外部类的实例对象,在没有外部类实例之前是无法创建内部类的
                 *  b.内部类是一个相对独立的实体,与外部类不是 is-a 关系
                 *  c.创建内部类的时刻并不依赖于外部类的创建
                 *
                 * 内部类作用:
                 * 1、内部类方法可以访问该类定义所在作用域中的数据,包括被private修饰的私有数据(内部类无条件访问外部类数据)
                 * public class DataOuterClass{
                 *     private String data = "外部类数据";
                 *     private class InnerClass{
                 *         public InnerClass(){
                 *             System.out.println(data);
                 *         }
                 *     }
                 *     public void getInner(){
                 *         new InnerClass();
                 *     }
                 *     public static void main(String args){
                 *         DataOuterClass OuterClass = new  DataOuterClass();
                 *         OuterClass.getInner();//外部类数据
                 *     }
                 * }
                 *
                 * 2、内部类可以对同一包中的其他类型隐藏(普通类不能使用 private,protected修饰;当内部类实现某个接口的时候,在向上转型,对外部来说就完全隐藏了接口的实现)
                 * 接口:
                 * public interface InnerInterface{
                 *     void innerMethod();
                 * }
                 * 具体类:
                 * public class OuterClass{
                 *     //private修饰内部类实现隐藏信息细节
                 *     private class InnerClass implements InnerInterface{
                 *          public void innerMethod(){
                 *              System.out.println("实现内部类隐藏");
                 *          }
                 *     }
                 *     public InnerInterface getInner(){
                 *         return new InnerClass();
                 *     }
                 * }
                 * 测试类:
                 * public class Test{
                 *     public static void main(String args){
                 *         OuterClass outerClass = new OuterClass();
                 *         InnerInterface inner = outerClass.getInner();
                 *         inner.innerMethod();//实现内部类隐藏
                 *     }
                 * }
                 *
                 * 3、可以解决java中单继承缺陷
                 * public class ExampleOne{
                 *     public String name(){
                 *         return "inner";
                 *     }
                 * }
                 * public class ExampleTwo{
                 *     public int age(){
                 *         return 25;
                 *     }
                 * }
                 * public class MainExample{
                 *     //内部类1继承 ExampleOne
                 *     private class InnerOne extends ExampleOne{
                 *         return super.name();
                 *     }
                 *     //内部类2继承 ExampleTwo
                 *     private class InnerTwo extends ExampleTwo{
                 *         public int age(){
                 *             return super.age();
                 *         }
                 *     }
                 *    public String name(){
                 *        return new InnerOne().name();
                 *    }
                 *    public int age(){
                 *        return new InnerTwo().age();
                 *    }
                 *    public static void main(String args){
                 *        MainExample mi = new MainExample();
                 *        System.out.println("姓名:"+ mi.name());
                 *        System.out.println("年龄:"+ mi.age());
                 *    }
                 * }
                 *
                 * 4、可以使用匿名内部类实现回调函数
                 *
                 * 其中 LineReader 为 Properties的一个内部类:具体操作如下
                 */
                //byte[] inByteBuf = new byte[8192]; //inByteBuf.length = 8192
                //System.out.println(":::"+is.read(new byte[8192]));-1
                //inLimit = (inStream==null) ? reader.read(inCharBuf) : inStream.read(inByteBuf); inLimit = -1
                //System.out.println(true || false);//true
                //char c = (char) (0xff & inByteBuf[inOff++]);
                //System.out.println("====:"+(char)(0xff & inByteBuf[0]));//相当于读取配置文件的第一个字符====:0
            } catch (Throwable t) {
                handleThrowable(t);
                log.warn(t);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    log.warn("Could not close catalina.properties", ioe);
                }
            }
        }

        if ((is == null)) {
            // Do something
            log.warn("Failed to load catalina.properties");
            // That's fine - we have reasonable defaults.
            properties = new Properties();
        }

        // Register the properties as system properties
        //将属性注册为系统属性,Enumeration是Hashtable的内部类
        /**
         * properties.propertyNames():
         *  返回此属性列表中所有键的枚举,如果尚未从主属性列表中找到名称相同的键,则在默认属性列表中包含不同的键.
         *  Return:此属性列表中所有键枚举,包括默认值中的键
         */
        Enumeration<?> enumeration = properties.propertyNames();
        /**
         * 测试该枚举是否包含更多元素.
         * <code>true</code> if and only if this enumeration object
         *   contains at least one more element to provide;
         *   <code>false</code> otherwise.
         */
        while (enumeration.hasMoreElements()) {
            System.out.println("====>><<依次遍历 catalina.properties 中所有的属性=="+enumeration.nextElement());
            String name = (String) enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value != null) {
                //将 catalina.properties 中对应的属性值设置为系统的属性值
                System.setProperty(name, value);
            }
        }
    }


    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }
}
