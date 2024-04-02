package proxy;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/27 13:28
 */
public class MethodInvokeTest {

    @Test
    public void test() throws Exception {
        Cat cat = new Cat();
        String food = "food";
        Method eatMethod = Cat.class.getMethod("eat", String.class);
        MethodAccess methodAccess = MethodAccess.get(Cat.class);
        int num = 30000000;

        long time3 = 0;
        for (int i = 0; i < num; i++) {
            long start = System.currentTimeMillis();
            methodAccess.invoke(cat, "eat", food);
            long end = System.currentTimeMillis();
            time3 += (end - start);
        }
        System.out.println("ASM反射调用：总" + time3 + "-" + time3 / num + "ms");
        long time4 = 0;
        for (int i = 0; i < num; i++) {
            long start = System.currentTimeMillis();
            int eatIndex = methodAccess.getIndex("eat", String.class);
            methodAccess.invoke(cat, eatIndex, food);
            long end = System.currentTimeMillis();
            time4 += (end - start);
        }
        System.out.println("ASM反射调用优化：总" + time4 + "-" + time4 / num + "ms");

        long time1 = 0;
        for (int i = 0; i < num; i++) {
            long start = System.currentTimeMillis();
            eatMethod.invoke(cat, food);
            long end = System.currentTimeMillis();
            time1 += (end - start);
        }
        System.out.println("普通反射调用：总" + time1 + "-" + time1 / num + "ms");

        long time2 = 0;
        for (int i = 0; i < num; i++) {
            long start = System.currentTimeMillis();
            eatMethod.setAccessible(true);
            eatMethod.invoke(cat, food);
            long end = System.currentTimeMillis();
            time2 += (end - start);
        }
        System.out.println("普通反射调用优化：总" + time2 + "-" + time2 / num + "ms");

        long time5 = 0;
        for (int i = 0; i < num; i++) {
            long start = System.currentTimeMillis();
            cat.eat(food);
            long end = System.currentTimeMillis();
            time5 += (end - start);
        }
        System.out.println("普通调用：总" + time5 + "-" + time5 / num + "ms");

    }

    @Test
    public void test2() {
        ProxyFactory byteBuddy = ExtensionLoader.loadService(ProxyFactory.class, "byteBuddy");
        ProxyFactory cglib = ExtensionLoader.loadService(ProxyFactory.class, "cglib");
        Cat cat = byteBuddy.createProxy(new Cat(), (proxy, method, args, superInvoker) -> superInvoker.invoke());
        Cat cat1 = cglib.createProxy(new Cat(), (proxy, method, args, superInvoker) -> superInvoker.invoke());
        System.out.println(cat);
    }
}
