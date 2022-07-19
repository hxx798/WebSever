package com.webserver.test;

/**
 * 测试单例模式
 */
public class TestSingleton {
    public static void main(String[] args) {
        Singleton singleton = Singleton.getSingleton();
        System.out.println(singleton);

        Singleton singleton1 = Singleton.getSingleton();
        System.out.println(singleton1);
    }
}
