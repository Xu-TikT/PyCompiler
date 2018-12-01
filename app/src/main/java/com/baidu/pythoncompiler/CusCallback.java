package com.baidu.pythoncompiler;


import android.content.Intent;

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
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        LogUtil.loge("faceDeceted 方法被调用");
        Intent intent = new Intent();
        intent.setAction("javaCalledByPython");
        intent.putExtra("methodValue",1);
        PyCompilerService.Host.sendBroadcast(intent);
        LogUtil.loge("faceDeceted 发送广播");
        return 20;
    }
}
