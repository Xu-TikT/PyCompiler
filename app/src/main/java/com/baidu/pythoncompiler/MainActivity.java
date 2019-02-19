package com.baidu.pythoncompiler;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private static String TAG = "MainActivity";
    public static MainActivity Host;
    public StarSrvGroupClass SrvGroup;
    private StarObjectClass python;
    private StarServiceClass service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Host = this;

//        copyJar();
//        initSrv();
        bindService(new Intent(this, PyCompilerService.class), conn, BIND_AUTO_CREATE);

    }

    private void initSrv() {
    /*----init starcore----*/
        StarCoreFactoryPath.StarCoreCoreLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreShareLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreOperationPath = "/data/data/" + getPackageName() + "/files";

        StarCoreFactory starcore = StarCoreFactory.GetFactory();
        service = starcore._InitSimple("test", "123", 0, 0);
        SrvGroup = (StarSrvGroupClass) service._Get("_ServiceGroup");
        service._CheckPassword(false);

		/*----run python code----*/
        SrvGroup._InitRaw("python34", service);//调用函数"_InitRaw"初始化python接口
        //使用函数"_ImportRawContext("python","",false,nil);"获取python全局原生对象,python类
        python = service._ImportRawContext("python", "", false, "");
        python._Call("import", "sys");

        StarObjectClass pythonSys = python._GetObject("sys");
        StarObjectClass pythonPath = (StarObjectClass) pythonSys._Get("path");
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files/python3.4.zip");
        pythonPath._Call("insert", 0, this.getApplicationInfo().nativeLibraryDir);
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files");
    }

    private void copyJar() {
        File destDir = new File("/data/data/" + getPackageName() + "/files");
        if (!destDir.exists())
            destDir.mkdirs();
        File python2_7_libFile = new File("/data/data/" + getPackageName() + "/files/python3.4.zip");
        if (!python2_7_libFile.exists()) {
            try {
                PyCompileUtils.copyFile(this, "python3.4.zip", null);
            } catch (Exception e) {
            }
        }
        try {
            PyCompileUtils.copyFile(this, "_struct.cpython-34m.so", null);
            PyCompileUtils.copyFile(this, "binascii.cpython-34m.so", null);
            PyCompileUtils.copyFile(this, "time.cpython-34m.so", null);
            PyCompileUtils.copyFile(this, "zlib.cpython-34m.so", null);
        } catch (Exception e) {
            System.out.println(e);
        }
        //----a test file to be read using python, we copy it to files directory
        try {
            PyCompileUtils.copyFile(this, "test.txt", "");
            PyCompileUtils.copyFile(this, "test_calljava.py", "");
            PyCompileUtils.copyFile(this, "thc_calljava.py", "");

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            //--load python34 core library first;
            System.load(this.getApplicationInfo().nativeLibraryDir + "/libpython3.4m.so");
        } catch (UnsatisfiedLinkError ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * python 调用 Java
     * <p>
     * 步骤说明：
     * 1. 将要执行的py代码copy到本地某个目录下
     * 2. 给py代码设置调用java回调类 JavaClass
     * 3. service_DoFile(xxx)
     * 即可执行python脚本，并调用Java类内容
     *
     * @param view
     */
    public void pyCallJava(View view) {
        String CorePath = "/data/data/" + getPackageName() + "/files";
        python._Set("JavaClass", CallBackClass.class);
        service._DoFile("python", CorePath + "/test_calljava.py", "");
    }

    private String pyCode = "def logMsg(msg):\n" +
            "    print(msg)";
    private String pyCode1 = "if 0 != 1:\n" +
            "  print('-------------thc11111')";

    private String pyCode2 = "def testread(name) :\n" +
            "    text_file = open(name, \"rt\")\n" +
            "    print(text_file.readline())\n" +
            "    text_file.close()";

    /**
     * Java 调用 python
     * <p>
     * 步骤说明：
     * 1. 调用python._Call("execute",xxx);执行python代码
     * 2. 然后就可以通过python._Call("xxx方法名",参数...)来调用python代码中的方法了
     *
     * @param view
     */
    public void javaCallPy(View view) {
        python._Call("execute", pyCode);
        python._Call("testread", "/data/data/" + getPackageName() + "/files/test.txt");
        python._Call("logMsg", "从Java传到Python");
    }

    private String thcCallJava =
            "import imp  #test load path\n" +
                    "\n" +
                    "import json\n" +
                    "print(JavaClass)\n" +
                    "\n" +
                    "val = JavaClass(\"from python\")\n" +
                    "\n" +
                    "\n" +
                    "print('执行人脸识别')\n" +
                    "age = val.faceDeceted()\n" +
                    "print(age)\n" +
                    "\n" +
                    "if age > 10:\n" +
                    "  print('你很年轻哦')\n" +
                    "else:\n" +
                    "    print('没有进入',age)\n" +
                    "\n" +
                    "print(\"===========end=========\")";

    /**
     * 执行python代码(字符串的形式)，同时又可以调用Java内容
     * <p>
     * 两种实现方式：
     * <p>
     * 1. 下载后台代码写到本地，执行文件，如同pyCallJava
     * <p>
     * 2. 设置Java回调类，直接运行String类型的Python代码
     *
     * @param view
     */
    public void javaAndPy(View view) {
        python._Set("JavaClass", CallBackClass.class);
//        python._Call("execute", thcCallJava);
        python._Call("execute", PyCodeFormat.buildPyCode(blocklyPyCode));
    }

    public void stopPyService(View view) {
        SrvGroup._ClearService();
    }

    Messenger mClientMessenger = null;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClientMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
        }
    };

//    private String blocklyPyCode = "if 0 <= 3:\n" +
//            "  print()\n"+
//            "  age = BlocklyJavascriptInterface.faceDeceted(1)\n" +
//            "  arrs = [1,2,3]\n" +
//            "  print('thc')\n" +
//            "  print(arrs[5])\n" +
//            "  a = 1/0\n" +
//            "  BlocklyJavascriptInterface.nprintList(arrs)\n" +
//            "  BlocklyJavascriptInterface.nprint(arrs[0])\n" +
//            "  print('Hello World!')";

//    private String blocklyPyCode =
//            "result = '人之初  性本善  性相近  习相远  苟不教  性乃迁 教之道  贵以专  \\\n" +
//            "\t昔孟母  择邻处  子不学  断机杼\\\n" +
//            "\t窦燕山  有义方  教五子  名俱扬  \\\n" +
//            "\t养不教  父之过 教不严  师之惰   \\\n" +
//            "\t 子不学 非所宜  幼不学  老何为\\\n" +
//            "\t 玉不琢  不成器  人不学  不知义  \\\n" +
//            "     为人子  方少时 亲师友  习礼仪 \\\n" +
//            "\t香九龄  能温席  孝于亲  所当执\\\n" +
//            "    融四岁  能让梨  弟于长  宜先知  \\\n" +
//            "    首孝弟  次见闻 知某数  识某文  \\\n" +
//            "\t一而十  十而百  百而千  千而万\\\n" +
//            "    三才者  天地人  三光者  日月星  \\\n" +
//            "    三纲者  君臣义父子亲  夫妇顺  \\\n" +
//            "\t曰春夏  曰秋冬  此四时  运不穷\\\n" +
//            "    曰南北  曰西东  此四方  应乎中 \\\n" +
//            "    曰水火  木金土此五行  本乎数  \\\n" +
//            "    曰仁义  礼智信  此五常  不容紊'\n" + "print(result)\n";

//    private String blocklyPyCode = "# -*- coding: UTF-8 -*-\n" +
//            " \n" +
//            "# Filename : test.py\n" +
//            "# author by : www.runoob.com\n" +
//            " \n" +
//            "num = float(input('请输入一个数字:3 '))\n" +
//            "num_sqrt = num ** 0.5\n" +
//            "print(' %0.3f 的平方根为 %0.3f'%(num ,num_sqrt))\n";

    private String blocklyPyCode = "print(1)\nBlocklyJavascriptInterface.tts(1)\n"
            + "BlocklyJavascriptInterface.tts('msg')\n"
            +"BlocklyJavascriptInterface.tts(BlocklyJavascriptInterface.PI)\n"
            +"a = BlocklyJavascriptInterface.log10(-1)\n"
            +"print(a)\n"
            +"BlocklyJavascriptInterface.t(a)\n"
            ;

    /**
     * 在Service中运行py
     *
     * @param view
     */
    public void runPyInService(View view) {

        Message obtain = Message.obtain();
        Bundle bundle = new Bundle();
        Log.e(TAG, "执行死循环代码");
        bundle.putString("data", PyCodeFormat.buildPyCode(blocklyPyCode));
        obtain.setData(bundle);
        try {
            mClientMessenger.send(obtain);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 杀掉进程
     *
     * @param view
     */
    public static void finishProcess(View view) {
        Log.e(TAG, "杀死进程");
        killProcess("com.baidu.pythoncompiler.remote");
    }

    private static void killProcess(String killName) {
        // 获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) Host.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取系统中所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        // 对系统中所有正在运行的进程进行迭代，如果进程名所要杀死的进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            String processName = appProcessInfo.processName;
            if (processName.equals(killName)) {
                killProcessByPid(appProcessInfo.pid);
            }
        }
    }

    /**
     * 根据要杀死的进程id执行Shell命令已达到杀死特定进程的效果
     *
     * @param pid
     */
    private static void killProcessByPid(int pid) {
        String command = "kill -9 " + pid + "\n";
        Runtime runtime = Runtime.getRuntime();
        Process proc;
        try {
            proc = runtime.exec(command);
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }


}
