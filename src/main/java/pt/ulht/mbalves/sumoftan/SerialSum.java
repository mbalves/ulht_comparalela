
package pt.ulht.mbalves.sumoftan;

import java.util.Random;

public class SerialSum {

    private static double[] globalArray;

    private static void arrayInit(int n) {
        globalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++) {
            globalArray[i] = randomNumber.nextDouble();
        }
    }

    public static void main(String[] args) {

        int nValues = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 100_000_000;

        double totalSum;
        long startTime, stopTime;

        // Initialize global Array
        arrayInit(nValues);

        // Do the serial sum
        startTime = System.currentTimeMillis();
        totalSum = sum(0, nValues, globalArray);
        stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("--------------- 1. Serial Sum ------------------");
        System.out.println("Count Items  = " + nValues);
        System.out.println("Total Sum    = " + totalSum);
        System.out.println("Elapsed Time = " + serialTime + "ms");
        System.out.println("--------------------------------------------------");
    }

    static private double sum(int begin, int end, double[] globalArray) {
        double mySum = 0.0;
        for (int i = begin; i < end; i++) {
            mySum += Math.tan(globalArray[i]);
        }
        return mySum;
    }
}

