package pt.ulht.mbalves.quickSort;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Quicksort serial version
 */
public class ParallelSort {

    private int length;

    public static void main(String args[]) {

        int nValues = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 100_000_000;

        int[] globalArray = new int[nValues];
        long startTime;
        long stopTime;

        Random randomNumber = new Random();
        for (int i = 0; i < nValues; i++) {
            globalArray[i] = randomNumber.nextInt(nValues);
        }
        ParallelSort sorter = new ParallelSort();

        System.out.println("Sorting " + globalArray.length + " values...");
        startTime = System.currentTimeMillis();
        sorter.sort(globalArray);
        stopTime = System.currentTimeMillis();
        long totalTime = stopTime - startTime;

        System.out.println("------------ 3. Sort Vector QuickSort (Parallel) ---------------");
        System.out.println("Count Items  = " + nValues);
        System.out.println("Elapsed Time = " + totalTime + "ms");
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < 100; i++)
            System.out.print(globalArray[i] + " ");
        System.out.println();

    }

    public void sort(int[] inputArr) {

        if (inputArr == null || inputArr.length == 0) {
            return;
        }
        length = inputArr.length;
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new FJSort(0, inputArr.length - 1, inputArr));

    }

}

final class FJSort extends RecursiveAction {
    static final int SEQUENTIAL_THRESHOLD = 5000;
    int lowerIndex;
    int higherIndex;
    int[] array;

    public FJSort(int lowerIndex,
                  int higherIndex,
                  int[] array) {
        this.lowerIndex = lowerIndex;
        this.higherIndex = higherIndex;
        this.array = array;
    }

    @Override
    protected void compute() {

        int i = lowerIndex;
        int j = higherIndex;
        // Use middle index number as pivot
        int pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (array[i] < pivot) {
                i++;
            }
            while (array[j] > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        FJSort left = null;
        FJSort right = null;

        if (lowerIndex < j) {
            if (j - lowerIndex <= SEQUENTIAL_THRESHOLD) {
                left = new FJSort(lowerIndex, j, array);
                left.fork();
            } else {
                higherIndex = j;
                compute();
            }
        }
        if (i < higherIndex) {
            if (higherIndex - i <= SEQUENTIAL_THRESHOLD) {
                right = new FJSort(i, higherIndex, array);
                right.fork();
            } else {
                lowerIndex = i;
                compute();
            }
        }
        if (left != null) left.join();
        if (right != null) right.join();
    }

    private void exchangeNumbers(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
