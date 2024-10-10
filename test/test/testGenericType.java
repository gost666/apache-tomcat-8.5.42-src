package test;


import java.util.ArrayList;
import java.util.List;

public class testGenericType {
    public static void main(String[] args) {
        Pair<Number> pair = new Pair<>(456, 123);
        int i = PairHelper.addPair(pair);
        System.out.println(i);

        ArrayList<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        printIntVal(integerList);

        ArrayList<Float> floatList = new ArrayList<>();
        floatList.add((float) 12);
        floatList.add((float) 121);
        printIntVal(floatList);

        GenericMethod genericMethod = new GenericMethod();
        List<String> str = new ArrayList<>();
        List<Integer> inte = new ArrayList<>();
        System.out.println(str.getClass() == inte.getClass());//true

        // 一、不显式地指定类型参数
        // (1)传入的两个实参都是 Integer,所以泛型方法中的<T> == <Integer>
        int aa = GenericMethod.add(1, 2);
        // (2)传入的两个实参一个是 Integer,另一个是 Float,所以<T>取共同父类的最小级,即: <T> == <Number>
        Number ff = GenericMethod.add(1, 1.2);
        // 传入的两个实参一个是 Integer,另一个是 String,所以<T>取共同父类的最小级,即: <T> == <Object>
        Object oo = GenericMethod.add(1, "asd");
        // 二、显式地指定类型参数
        // (1)指定了<T> = <Integer>,所以传入的实参只能为 Integer 对象
        int ss = GenericMethod.<Integer>add(1, 2);
        // (2)指定了<T> = <Integer>,所以不能传入 Float 对象
        //int ll = GenericMethod.<Integer>add(1, 2.2);//编译错误
        // (3)指定<T> = <Number>,所以可以传入 Number 对象;Integer 和 Float 都是 Number 的子类,因此可以传入两者的对象
        Number gg = GenericMethod.<Number>add(1, 2.2);

        String s = genericMethod.fun("Tom");
        int ii = genericMethod.fun(30);
        //System.out.println(s);//Tom
        //System.out.println(ii);//30

    }
    public static void printIntVal(List<? extends Number> list){
        for (Number number : list) {
            System.out.println(number.intValue());
        }
    }
}
