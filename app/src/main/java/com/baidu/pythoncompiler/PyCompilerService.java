package com.baidu.pythoncompiler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import java.io.File;

public class PyCompilerService extends Service {

    private static final String TAG = "PyCompilerService";

    public StarSrvGroupClass SrvGroup;
    private static StarObjectClass python;
    private StarServiceClass service;
    static ServiceHandler serviceHandler = new ServiceHandler();

    public static PyCompilerService Host;

    private Messenger messenger = new Messenger(serviceHandler);

    public PyCompilerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Host = this;
        Log.e(TAG, "packageName:\t" + getPackageName());
        copyJar();
        initSrv();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("killpython");
        registerReceiver(new KillSelfReceiver(), intentFilter);

        IntentFilter javaCalledByPy = new IntentFilter();
        javaCalledByPy.addAction("javaCalledByPython");
        registerReceiver(new JavaCalledByPyReceiver(), javaCalledByPy);
    }

    public class KillSelfReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "收到了杀死进程的广播");
            SrvGroup._ClearService();
            System.exit(0);
        }
    }


    public class JavaCalledByPyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int methodValue = intent.getIntExtra("methodValue", -1);
            LogUtil.loge("收到广播 方法值：" + methodValue);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return new MyBinder();
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
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

    static class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "要执行的代码：\t" + msg.getData().getString("data"));
            python._Set("JavaClass", CusCallback.class);
            python._Call("execute", msg.getData().getString("data"));
        }
    }


}
