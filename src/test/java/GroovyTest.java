import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;



public class GroovyTest
{
    /**
     * 测试groovy脚本
     * 
     * @author 刘庆志
     */
    private void testGroovyScript()
    {
        Binding binding = new Binding();
        binding.setVariable("foo", new Integer(2));
        GroovyShell shell = new GroovyShell(binding);

        Object value = shell.evaluate("println 'Hello World!'; x = 123; return foo * 10");
        System.out.println(value); 
        System.out.println(binding.getVariable("x"));
    }
    /**
     * 测试groovy类
     * 
     * @author 刘庆志
     */
    private void testGroovyWithBaseClass()
    {
        String groovySrc="\n" +
                "package com.yesmywine.lqz\n" +
                "import com.yesmywine.test.TestInterface1\n" +
                
                "public class Tester implements TestInterface1 {\n" +
                "public String printStr(String src,Integer srcInteger) {\n" +
                "println \"刘庆志的测试：this is in the test class\"+src+\",\"+srcInteger ;\n" +
                "return src+\"刘庆志的测试\";" +
                "}\n" +
                "}";
        
        String paramSrc="要打印的字符串";
        Integer srcInteger=10;
        
        try
        {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(groovySrc, "SomeName.groovy");
            Object aScript = clazz.newInstance();
            TestInterface1 myObject = (TestInterface1) aScript;
            String groovyReturn=myObject.printStr(paramSrc, srcInteger);
            
            System.out.println(myObject);
            System.out.println(groovyReturn);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
}
