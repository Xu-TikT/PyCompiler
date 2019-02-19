package com.baidu.pythoncompiler;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testPyFormat() {
        String py = "println('执行人脸识别')\n" +
                "age = val.faceDeceted()\n" +
                "println(age)\n" +
                "\n" +
                "if 0 > 1:\n" +
                "  println('你很年轻哦')\n" +
                "else:\n" +
                "    println('没有进入',age)\n" +
                "\n" +
                "println(\"===========end=========\")";
        String code = PyCodeFormat.buildPyCode(py);
        System.out.println(code);
    }

    @Test
    public void testSqrt() {
        System.out.println("--log10-->>> " + Math.log10(-10));
        System.out.println("--round-->>> " + Math.round(-10));
        System.out.println("--ceil-->>> " + Math.ceil(-10));
        System.out.println("--floor-->>> " + Math.floor(-10));
        System.out.println("--sqrt-->>> " + Math.sqrt(-10));
        System.out.println("--in-->>> " + Math.log(-10));
        System.out.println("--fabs-->>> " + Math.abs(-10));
        System.out.println("--log10-->>> " + Math.log10(-10));
        System.out.println("--exp-->>> " + Math.exp(-10));
        System.out.println("--sin-->>> " + Math.sin(-10));
        System.out.println("--cos-->>> " + Math.cos(-10));
        System.out.println("--tan-->>> " + Math.tan(-10));
        System.out.println("--asin-->>> " + Math.asin(-10));
        System.out.println("--acos-->>> " + Math.acos(-10));
        System.out.println("--atan-->>> " + Math.atan(-10));

    }

    @Test
    public void testRandit() {
        int max = Math.max(80, 120);
        int min = Math.min(80, 120);
        int i = (int) (min + Math.random() * (max - min + 1));
        System.out.print(i);
    }

    // 过滤特殊字符
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @Test
    public void testStringFilter() {
//        String str = "*adCVs*34_a _09_b5*[/435^*&城池()^$$&*).{}+.|.)%%*(*.中国}34{45[]12.fd'*&999下面是中文的字符￥……{}【】。，；’“‘”？";
//        System.out.println(str);
//        System.out.println(StringFilter(str));
        System.out.print(Math.ceil(3.2));
    }
}