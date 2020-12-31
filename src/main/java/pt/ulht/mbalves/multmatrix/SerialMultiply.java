package pt.ulht.mbalves.multmatrix;

public class SerialMultiply {

    public static void main(String[] args) {

        int n = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 1_500;

        int heightA = n;
        int widthA = n;
        int heightB = n;
        int widthB = n;

        float[][] firstMatrix = new float[widthA][heightA];
        float[][] secondMatrix = new float[widthB][heightB];

        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                firstMatrix[i][j] = i * n + j; //1.0f;
            }
        }
        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                secondMatrix[i][j] = i * n + j; //1.0f;
            }
        }
        System.out.println("Computing multiply 2 matrices " + n + " squared...");

        // Multiply Two matrices
        long time = System.currentTimeMillis();
        float[][] product = multiplyMatrices(firstMatrix, secondMatrix, heightA, widthA, widthB);
        long serialTime = System.currentTimeMillis() - time;

        System.out.println("------------ 2. Multiply 2 Matrices (Serial) ---------------");
        System.out.println("Result Matrix  = " + n + " x " + n);
        System.out.println("Elapsed Time   = " + serialTime + "ms");
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

    public static float[][] multiplyMatrices(float[][] firstMatrix, float[][] secondMatrix, int heightA, int widthA, int widthB) {
        float[][] product = new float[heightA][widthB];
        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                for (int k = 0; k < widthA; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }
        return product;
    }
}
