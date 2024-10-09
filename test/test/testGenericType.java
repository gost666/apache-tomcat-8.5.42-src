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

    }
    public static void printIntVal(List<? extends Number> list){
        for (Number number : list) {
            System.out.println(number.intValue());
        }
    }
}
