package com.baidu.pythoncompiler;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tianhouchao on 2018/10/17.
 */

public class PyCompileUtils {
    public static void copyFile(Context c, String Name, String desPath) throws IOException {
        File outfile = null;
        if (desPath != null)
            outfile = new File("/data/data/" + c.getPackageName() + "/files/" + desPath + Name);
        else
            outfile = new File("/data/data/" + c.getPackageName() + "/files/" + Name);
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
}
