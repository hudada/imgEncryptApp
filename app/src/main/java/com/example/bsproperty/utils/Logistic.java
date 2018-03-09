package com.example.bsproperty.utils;

/**
 * Created by John on 2018/3/9.
 */

public class Logistic {

    private static double u = 3.76;
    private static Double x0 = 0.001;
    private static int loop = 64;

    public static Double[] getKey() {
//    	x[k+1] = u*x[k]*(1-x[k])
        Double[] key = new Double[loop];
        key[0] = x0;
        for (int i = 1; i < loop; i++) {
            key[i] = u * key[i - 1] * (1 - key[i - 1]);
        }
        return key;
    }
}
