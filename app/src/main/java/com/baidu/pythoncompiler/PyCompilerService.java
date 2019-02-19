package com.baidu.pythoncompiler;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarMsgCallBackInterface;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PyCompilerService extends Service {

    private static final String TAG = "PyCompilerService";

    public StarSrvGroupClass SrvGroup;
    private static StarObjectClass python;
    private static StarServiceClass service;
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

        final StarCoreFactory starcore = StarCoreFactory.GetFactory();
        starcore._RegMsgCallBack_P(new StarMsgCallBackInterface() {
            public Object Invoke(int ServiceGroupID, int uMes, Object wParam, Object lParam) {
                if (uMes == starcore._GetInt("MSG_DISPMSG") || uMes == starcore._GetInt("MSG_DISPLUAMSG")) {

                    String result = (String) wParam;
                    if (!TextUtils.isEmpty(result)) {
                        if (result.startsWith("[warn")) {
                            LogUtil.loge("打印错误日志：" + wParam);
                        } else {
                            LogUtil.loge("打印print内容：" + wParam);
                        }
                    }
                    System.out.println("called++++++++++++++++" + (String) wParam);
                }
                return null;
            }
        });
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

            python._GetLastError();
            int i = python._GetActiveCmd();

            String refInfo = python._GetRefInfo();
            Log.e(TAG, "refInfo：\t" + refInfo);


            String thc = service._GetStr("thc");
            Log.e(TAG, "GetStr：\t" + thc);

            String filePath = Host.getExternalCacheDir() + File.separator;
            File file = new File(filePath + "thc.log");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "file地址：\t" + filePath);
            service._SetLogFile(Host.getExternalCacheDir() + File.separator + "thc.log");

            String logFile = service._GetLogFile();
            Log.e(TAG, "logFile：\t" + logFile);

            Log.e(TAG, "要执行的代码：\t" + msg.getData().getString("data"));
            python._Set("JavaClass", CusCallback.class);
            python._Call("execute", msg.getData().getString("data"));
            Object getastErrorInfo = python._Call("GetastErrorInfo");

            String s = python._GetLastErrorInfo();
            LogUtil.loge("打印的error：" + s);

        }
    }

    public void finishProcess() {
        Log.e(TAG, "杀死进程");
        killProcess("com.baidu.pythoncompiler.remote");
    }

    private void killProcess(String killName) {
        // 获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
    private void killProcessByPid(int pid) {
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
