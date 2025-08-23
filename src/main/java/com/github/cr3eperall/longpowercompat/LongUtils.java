package com.github.cr3eperall.longpowercompat;

public class LongUtils {
    public static long saturatedSum(long a, long b) {
        long sum = a+b;
        if (((a ^ sum) & (b ^ sum)) < 0) {
            sum = a > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
        return sum;
    }
    public static long saturatedMul(long a, long b) {
        if (a > 0 && b > 0 && a > Long.MAX_VALUE / b) {
            return Long.MAX_VALUE;
        } else if (a < 0 && b < 0 && a < Long.MAX_VALUE / b) {
            return Long.MAX_VALUE;
        } else if (a > 0 && b < 0 && b < Long.MIN_VALUE / a) {
            return Long.MIN_VALUE;
        } else if (a < 0 && b > 0 && a < Long.MIN_VALUE / b) {
            return Long.MIN_VALUE;
        }
        return a * b;
    }
}
