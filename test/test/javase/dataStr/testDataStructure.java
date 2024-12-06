package test.javase.dataStr;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class testDataStructure {

    @Test
    public void testMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("b", 12012);
        map.put("a", 1);
        map.put("c", 13);
        map.put("d", 10);
        map.put("e", 19);
        map.put("f", 17);
        map.put("g", 16);
        map.put("h", 15);
        map.put("i", 14);
        map.put("j", 9);
        map.put("k", 8);
        map.put("l", 730);
        map.put("m", 60);// if (++size > threshold) 返回true;执行 resize()
        map.put("n", 534);
        map.put("o", 4400);
        map.put("p", 3);
        map.put("q", 120);
        map.put("r", 100);
        map.put("s", 201);
        map.put("t", 21000);
        map.put("u", 202);
        map.put("v", 26);
        map.put("w", 29);
        map.put("x", 232);
        map.put("y", 212);// if (++size > threshold) 返回true;执行 resize()
        map.put("z", 210);
        map.put("z1", 1000);//if ((tab = table) == null || (n = tab.length) == 0) 返回 false;if ((p = tab[i = (n - 1) & hash]) == null) 返回 false
        map.put("z2", 2100);
        map.put("z3", 2001);
        map.put("z4", 12345);
        map.put("z5", 90);
        map.put("z6", 800);
        map.put("z7", 980);
        map.put("z8", 12300);
        map.put("z9", 4200);
        map.put("z0", 1001);
        map.put("y1", 21400);
        map.put("y2", 2200);
        map.put("y3", 1200);
        map.put("y4", 2104);
        map.put("y5", 7899);
        map.put("y6", 900);
        map.put("y7", 1040);
        map.put("y8", 1020);
        map.put("y9", 190);
        map.put("y10", 180);
        map.put("y11", 170);
        map.put("y12", 160);
        map.put("y13", 15012);
        map.put("y14", 166);
        map.put("y15", 231);
        map.put("y16", 145);
        map.put("y17", 127);
        map.put("y18", 221);
        map.put("y19", 345);
        map.put("y20", 819);
        map.put("y21", 56);
        map.put("y22", 506);
        map.put("y23", 709);
        map.put("y24", 12);
        map.put("y25", 1010);
        map.put("y26", 132);
        map.put("y27", 456);
        map.put("y28", 654);
        map.put("y29", 90010);
        map.put("y31", 6782);
        map.put("y30", 3975);
        map.put("y32", 125621);
        map.put("y33", 956);
        map.put("y34", 6103);
        map.put("y35", 5678);
        map.put("y36", 1634);
        map.put("y37", 2890);
        map.put("y38", 216);
        map.put("y39", 907);
        map.put("y41", 21453);
        map.put("y42", 784);
        map.put("y43", 1013);
        map.put("y44", 9109);
        map.put("y45", 1007);
        map.put("y46", 2564);
        map.put("y47", 12309);
        map.put("y48", 205);
        map.put("y49", 78965);
        map.put("y50", 2354);
        map.put("y51", 8712);
        map.put("y52", 6389);
        map.put("y53", 5671);
        map.put("y54", 9031);
        map.put("y55", 658);
        map.put("y56", 8945);
        map.put("y57", 235);
        map.put("y58", 570);
        map.put("y59", 124001);
        map.put("y60", 562);
        map.put("y61", 12893);
        map.put("y62", 100122);
        map.put("y63", 14561);
        map.put("y64", 1978);
        map.put("y65", 1090);
        map.put("y66", 2078);
        map.put("y67", 1256);
        map.put("y68", 834);
        map.put("y69", 700);





        /**
         * HashMap put()原理:
         * 1.默认容量为:16;默认加载因子为:0.75
         *  1.1 当map中的元素个数大于 threshold = DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR (12 = 16 * 0.75) 时,
         *      map开始 resize();此时 threshold=24
         *
         * 2.HaspMap由数组+链表+红黑树构成
         * 3.
         *
         */

        /**
         * HashMap 第一次resize():
         * 1.判断 oldTab(表示扩容前的map) 是否为 null(是否有元素);如果为 null,则返回 0;否则返回 oldTab(表示扩容前的map) 的容量(int oldCap = (oldTab == null) ? 0 : oldTab.length;)
         * 2.如果 oldTab(表示扩容前的map) 大于 0
         *  2.1 继续判断 oldTab(表示扩容前的map) 是否大于 1073741824(MAXIMUM_CAPACITY);如果大于,则设置下次扩容条件(Integer.MAX_VALUE)并返回map
         *  2.2 先按照 (32 = 16 << 1)2倍 扩容后判断是否小于 1073741824(MAXIMUM_CAPACITY) 并且 oldTab(表示扩容前的map) 大于等于 DEFAULT_INITIAL_CAPACITY(16),如果同时满足上述两者条件,则下次扩容条件设置为 24(oldThr << 1)
         * 3.创建一个容量为32的map,判断 oldTab(表示扩容前的map) 是否为 null;
         *  3.1 如果为 null,则返回新创建的map
         *  3.2 如果不为 null,遍历 oldTab(表示扩容前的map) 中的数据(通过 for (int j = 0; j < oldCap(16); ++j) {...} 循环遍历)
         *      3.2.1 判断 oldTab(表示扩容前的map) 中每一个位置的数据是否不等于 null,如果不为 null,则清空原map中每个位置的数据
         *          3.2.1.1 判断 oldTab(表示扩容前的map) 位置的下一个节点是否还有元素;如果没有,就将 oldTab(表示扩容前的map) 第0个位置的数据放到 扩容后的map中(newTab[e.hash & (newCap - 1)] = e;)
         *
         *
         */
    }
}
