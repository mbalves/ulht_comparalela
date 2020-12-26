/**
 * This program computes the sum of the trigonometric tangents of N random numbers in serial
 * <p>
 * Created by Jose Rogado on 17-12-2021.
 */
package pt.ulht.mbalves.sumoftan;

import java.util.Random;

public class ParallelSum {

    private static double[] globalArray;

    private static void arrayInit(int n) {
        globalArray = new double[n];
        Random randomNumber = new Random();
        for (int i = 0; i < n; i++) {
            globalArray[i] = randomNumber.nextDouble();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        int nValues = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 100_000_000;

        double totalSum = 0.0;
        long startTime, stopTime;

        // Initialize global Array
        arrayInit(nValues);

        // Do the serial sum
        startTime = System.currentTimeMillis();
        int nCores = Runtime.getRuntime().availableProcessors();
        int part = nValues / nCores;
        SumThread[] threads = new SumThread[nCores];
        for (int i = 0; i < nCores; i++) {
            threads[i] = new SumThread("Thread " + i, part * i, part * (i + 1), globalArray);
            threads[i].start();
        }
        for (int i = 0; i < nCores; i++) {
            threads[i].join();
            totalSum += threads[i].getTotal();
        }

        stopTime = System.currentTimeMillis();
        long totalTime = stopTime - startTime;
        System.out.println("--------------- 2. Parallel Sum ------------------");
        System.out.println("Count Items  = " + nValues);
        System.out.println("Total Sum    = " + totalSum);
        System.out.println("Elapsed Time = " + totalTime + "ms");
        System.out.println("--------------------------------------------------");
    }
}

final class SumThread extends Thread {
    private String name;
    private int start;
    private int end;
    private double[] numbers;
    private double total;

    SumThread(String name, int start, int end, double[] numbers) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.numbers = numbers;
    }

    public double getTotal() {
        return this.total;
    }

    @Override
    public void run() {
        total = 0.0;
        System.out.println(name + " : Start (" + start + " to " + end + ")");
        for (int i = start; i < end; i++) {
            total += Math.tan(numbers[i]);
        }
        System.out.println(name + " : Finished - " + total);
    }
}

