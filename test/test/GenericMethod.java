package test;

public class GenericMethod {
    //普通的泛型方法
    public <T> T fun(T t) {//可以接受任意类型的数据
        return t;
    }
    //静态的泛型方法
    public static <E> void show(E one) {
        System.out.println("静态方法:" + one);
    }

    public static <T> T add(T x,T y) {
        return y;
    }
}