package com.lagou.edu;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println(Thread.currentThread().getContextClassLoader().getClass());
        System.out.println(1);
    }
}
