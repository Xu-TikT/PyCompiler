package com.baidu.pythoncompiler;

import org.junit.Test;

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
    public void testPyFormat(){
        String py = "print('执行人脸识别')\n" +
                "age = val.faceDeceted()\n" +
                "print(age)\n" +
                "\n" +
                "if 0 > 1:\n" +
                "  print('你很年轻哦')\n" +
                "else:\n" +
                "    print('没有进入',age)\n" +
                "\n" +
                "print(\"===========end=========\")";
        String code = PyCodeFormat.buildPyCode(py);
        System.out.print(code);
    }
}