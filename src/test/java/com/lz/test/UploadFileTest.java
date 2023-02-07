package com.lz.test;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test1(){
        String filename="error.jpg";
        String s = filename.substring(filename.lastIndexOf("."));
        System.out.println(s);
    }
}
