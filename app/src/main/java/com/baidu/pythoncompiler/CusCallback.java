package com.baidu.pythoncompiler;


/**
 * Created by tianhouchao on 2018/10/18.
 */

public class CusCallback {
    public CusCallback(String Info) {
        System.out.println(Info);
    }

    public String test() {
        return "------------------custom callback";
    }

    public int faceDeceted() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20;
    }
}
