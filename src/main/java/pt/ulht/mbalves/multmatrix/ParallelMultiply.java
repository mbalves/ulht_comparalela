package pt.ulht.mbalves.multmatrix;

public class ParallelMultiply {

    public static void main(String[] args) throws InterruptedException {

        int n = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 1_500;

        int heightA = n;
        int widthA = n;
        int heightB = n;
        int widthB = n;

        float[][] firstMatrix = new float[widthA][heightA];
        float[][] secondMatrix = new float[widthB][heightB];

        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                firstMatrix[i][j] = 1.0f;
            }
        }
        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                secondMatrix[i][j] = 1.0f;
            }
        }
        System.out.println("Computing multiply 2 matrices with " + n + " values...");

        // Multiply Two matrices
        long time = System.currentTimeMillis();
        float[][] product = multiplyMatrices(firstMatrix, secondMatrix, heightA, widthA, widthB);
        long serialTime = System.currentTimeMillis() - time;

        System.out.println("------------ 2. Multiply 2 Matrices (Parallel) ---------------");
        System.out.println("Result Matrix  = " + n + " x " + n);
        System.out.println("Elapsed Time = " + serialTime + "ms");
        System.out.println("-------------------------------------------------------");

        System.out.println();
        int size = product.length > 10 ? 10 : product.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(product[row][col] + "    ");
            }
            System.out.println();
        }
        System.out.println("...");
    }

    public static float[][] multiplyMatrices(float[][] firstMatrix, float[][] secondMatrix, int heightA, int widthA, int widthB) throws InterruptedException {
        float[][] product = new float[heightA][widthB];

        int nCores = Runtime.getRuntime().availableProcessors();
        int part = heightA / nCores;
        MultiThread[] threads = new MultiThread[nCores];
        for (int i = 0; i < nCores - 1; i++) {
            threads[i] = new MultiThread("Thread " + i, part * i, part * (i + 1), firstMatrix, secondMatrix, product);
            threads[i].start();
        }
        // Last part use the main thread
        threads[nCores - 1] = new MultiThread("Thread " + (nCores - 1), part * (nCores - 1), part * (nCores), firstMatrix, secondMatrix, product);
        threads[nCores - 1].run();

        for (int i = 0; i < nCores - 1; i++) {
            threads[i].join();
        }

        return product;
    }

}

final class MultiThread extends Thread {
    private String name;
    private int start;
    private int end;
    float[][] firstMatrix;
    float[][] secondMatrix;
    private float[][] product;

    MultiThread(String name, int start, int end, float[][] firstMatrix, float[][] secondMatrix, float[][] product) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.product = product;
    }

    @Override
    public void run() {

        System.out.println(name + " : Start (" + start + " to " + end + ")");
        for (int i = start; i < end; i++) {
            for (int j = 0; j < product[0].length; j++) {
                for (int k = 0; k < firstMatrix[0].length; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }
        System.out.println(name + " : Finished.");
    }
}

