package com.example.bsproperty.utils;

/**
 * Created by John on 2018/3/9.
 */

public class Logistic {

    private static double u = 4;
    private static int loop = 8*8;

    public static int[] getKey(long key) {
//    	x[k+1] = u*x[k]*(1-x[k])
        Double[] keys = new Double[loop];
        Double x0 = key / Math.pow(10, 8);
        keys[0] = x0;
        for (int i = 1; i < loop; i++) {
            keys[i] = u * keys[i - 1] * (1 - keys[i - 1]);
        }

        int[] keys2 = new int[loop];

        for (int i = 0; i < keys.length; i++) {
            keys2[i] = (int) (keys[i] * Math.pow(2, 24));
        }

        return keys2;
    }

    public static Double[] getKey2(long key) {
//    	x[k+1] = u*x[k]*(1-x[k])
        Double[] keys = new Double[loop];
        Double x0 = key / Math.pow(10, 8);
        keys[0] = x0;
        for (int i = 1; i < loop; i++) {
            keys[i] = u * keys[i - 1] * (1 - keys[i - 1]);
        }

        return keys;
    }
}
