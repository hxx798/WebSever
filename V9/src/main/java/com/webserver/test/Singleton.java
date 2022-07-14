package com.webserver.test;

/**
 * JAVA 23种设计模式之一:单例模式
 * 使用该模式编写的类，全局最多只会有一个实例存在。
 *
 * 实现步骤:
 * 1:将构造器私有化(目的是杜绝外界可以通过构造方法肆意创建对象)
 * 2:提供一个私有的静态属性，是当前类型的。
 * 3:在静态块种对步骤2定义的属性初始化(静态块只调用一次，因此仅new一个当前类的实例)
 * 4:对外提供一个公开的，静态的方法可以获取当前类实例(返回该静态属性)
 */
public class Singleton {
    private static Singleton singleton;
    static {
        singleton = new Singleton();
    }
    private Singleton(){

    }
    public static Singleton getSingleton(){
        return singleton;
    }
}
