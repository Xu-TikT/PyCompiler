package com.baidu.pythoncompiler;

/**
 * Created by tianhouchao on 2018/10/18.
 */

public class PyCodeFormat {

    public static final String IMPORT = "import imp  #test load path";
    public static final String IMPORT_OS = "import os  #test load path";
    public static final String IMPORT_TIME = "import time  #test load path";
    public static final String ENTER = "\n";
    public static final String JAVA_CLASS = "BlocklyJavascriptInterface = JavaClass('init java callbackclass in python')";

    public static String buildPyCode(String pyCode){
        StringBuilder sb = new StringBuilder();
        sb.append(IMPORT);
        sb.append(ENTER);
        sb.append(IMPORT_OS);
        sb.append(ENTER);
        sb.append(IMPORT_TIME);
        sb.append(ENTER);
        sb.append(JAVA_CLASS);
        sb.append(ENTER);
        sb.append(pyCode);
        return sb.toString();
    }


}
