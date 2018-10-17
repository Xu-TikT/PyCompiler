package com.baidu.pythoncompiler;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {


    public static MainActivity Host;
    public StarSrvGroupClass SrvGroup;

    private void copyFile(Activity c, String Name, String desPath) throws IOException {
        File outfile = null;
        if (desPath != null)
            outfile = new File("/data/data/" + getPackageName() + "/files/" + desPath + Name);
        else
            outfile = new File("/data/data/" + getPackageName() + "/files/" + Name);
        //if (!outfile.exists()) {
        outfile.createNewFile();
        FileOutputStream out = new FileOutputStream(outfile);
        byte[] buffer = new byte[1024];
        InputStream in;
        int readLen = 0;
        if (desPath != null)
            in = c.getAssets().open(desPath + Name);
        else
            in = c.getAssets().open(Name);
        while ((readLen = in.read(buffer)) != -1) {
            out.write(buffer, 0, readLen);
        }
        out.flush();
        in.close();
        out.close();
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Host = this;

        File destDir = new File("/data/data/" + getPackageName() + "/files");
        if (!destDir.exists())
            destDir.mkdirs();
        java.io.File python2_7_libFile = new java.io.File("/data/data/" + getPackageName() + "/files/python3.4.zip");
        if (!python2_7_libFile.exists()) {
            try {
                copyFile(this, "python3.4.zip", null);
            } catch (Exception e) {
            }
        }
        try {
            copyFile(this, "_struct.cpython-34m.so", null);
            copyFile(this, "binascii.cpython-34m.so", null);
            copyFile(this, "time.cpython-34m.so", null);
            copyFile(this, "zlib.cpython-34m.so", null);
        } catch (Exception e) {
            System.out.println(e);
        }
        //----a test file to be read using python, we copy it to files directory
        try {
            copyFile(this, "test.txt", "");
            copyFile(this, "test_calljava.py", "");
        } catch (Exception e) {
            System.out.println(e);
        }
        /*----load test.py----*/
        String pystring = null;
        try {
            AssetManager assetManager = getAssets();
            InputStream dataSource = assetManager.open("test.py");
            int size = dataSource.available();
            byte[] buffer = new byte[size];
            dataSource.read(buffer);
            dataSource.close();
            pystring = new String(buffer);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            //--load python34 core library first;
            System.load(this.getApplicationInfo().nativeLibraryDir + "/libpython3.4m.so");
        } catch (UnsatisfiedLinkError ex) {
            System.out.println(ex.toString());
        }

        /*----init starcore----*/
        StarCoreFactoryPath.StarCoreCoreLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreShareLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreOperationPath = "/data/data/" + getPackageName() + "/files";

        StarCoreFactory starcore = StarCoreFactory.GetFactory();
        StarServiceClass Service = starcore._InitSimple("test", "123", 0, 0);
        SrvGroup = (StarSrvGroupClass) Service._Get("_ServiceGroup");
        Service._CheckPassword(false);

		/*----run python code----*/
        SrvGroup._InitRaw("python34", Service);//调用函数"_InitRaw"初始化python接口
        StarObjectClass python = Service._ImportRawContext("python", "", false, "");//使用函数"_ImportRawContext("python","",false,nil);"获取python全局原生对象,python类
        python._Call("import", "sys");

        StarObjectClass pythonSys = python._GetObject("sys");
        StarObjectClass pythonPath = (StarObjectClass) pythonSys._Get("path");
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files/python3.4.zip");
        pythonPath._Call("insert", 0, this.getApplicationInfo().nativeLibraryDir);
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files");

        python._Call("execute", pystring);//使用函数"XX._Call("funcname",...)"或者"XX._Get/_Set("Variable")"调用python函数,设置/获取python变量数值
        python._Call("testread", "/data/data/" + getPackageName() + "/files/test.txt");

        String CorePath = "/data/data/" + getPackageName() + "/files";
        python._Set("JavaClass", CallBackClass.class);
        Service._DoFile("python", CorePath + "/test_calljava.py", "");
    }


}
