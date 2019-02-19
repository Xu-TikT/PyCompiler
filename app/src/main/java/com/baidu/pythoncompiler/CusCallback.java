package com.baidu.pythoncompiler;


import android.content.Intent;
import android.widget.Toast;

/**
 * Created by tianhouchao on 2018/10/18.
 */

public class CusCallback {

    public double PI = Math.PI;

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
        int i = 0;
//        while (true) {
//            LogUtil.loge("死循环中.....");
//            if (i > 1) {
//                break;
//            }
//        }
        Intent intent = new Intent();
        intent.setAction("javaCalledByPython");
        intent.putExtra("methodValue", 1);
        PyCompilerService.Host.sendBroadcast(intent);
        LogUtil.loge("faceDeceted 发送广播");
        return 20;
    }

    public void nprint(Object o) {
        LogUtil.loge("本地打印：" + o);
    }

    public void nprintList(Object[] strs) {
        for (int i = 0; i < strs.length; i++) {
            LogUtil.loge("本地打印：" + strs[i]);
        }
    }

    public void tts(String msg) {
        LogUtil.loge("调用这个方法String类型：" + msg);
    }

    public void tts(int msg) {
        LogUtil.loge("调用这个方法Int类型：" + msg);
    }

    public void tts(double msg) {
        LogUtil.loge("调用这个方法double类型：" + msg);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tts(Object msg) {
        LogUtil.loge("调用这个方法Object类型：" + msg);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void t(String msg){
        Toast.makeText(PyCompilerService.Host,msg,Toast.LENGTH_SHORT).show();
    }

    public double log10(double value){
        if(value < 0){
            PyCompilerService.Host.finishProcess();
            return 0;
        }
        return Math.log10(value);
    }
}



