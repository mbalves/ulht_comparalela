package pt.ulht.mbalves.multmatrix;

import org.jocl.*;

import static org.jocl.CL.*;

public class JOCLMultiply {
    /**
     * The source code of the OpenCL program to execute
     */
    private static String programSource =
            "__kernel void " +
                    "sampleKernel(__global const float *a," +
                    "             __global const float *b," +
                    "             __global float *c," +
                    "             const uint n)" +
                    "{" +
                    "    int gid = get_global_id(0);" +
                    "    int lin = gid / n;" +
                    "    int col = gid % n;" +
                    "    c[gid] = 0;" +
                    "    for (int i=0; i<n; i++) {" +
                    "      c[gid] += a[lin*n+i] * b[i*n+col]; " +
                    "    }" +
                    "}";


    /**
     * The entry point of this sample
     *
     * @param args Not used
     */
    public static void main(String args[]) {
        int n = (args.length > 0 && args[0] != null) ? Integer.parseInt(args[0]) : 1_500;

        int heightA = n;
        int widthA = n;
        int heightB = n;
        int widthB = n;

        float[] firstMatrix = new float[widthA * heightA];
        float[] secondMatrix = new float[widthB * heightB];

        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                firstMatrix[i * n + j] = i * n + j; //1.0f;
            }
        }
        for (int i = 0; i < heightA; i++) {
            for (int j = 0; j < widthB; j++) {
                secondMatrix[i * n + j] = i * n + j; //1.0f;
            }
        }
        float[] product = new float[heightA * widthB];

        Pointer srcA = Pointer.to(firstMatrix);
        Pointer srcB = Pointer.to(secondMatrix);
        Pointer dst = Pointer.to(product);

        System.out.println("Computing multiply 2 matrices with " + n + " values...");

        // Multiply Two matrices
        long time = System.currentTimeMillis();
        multiplyMatrices(n, srcA, srcB, dst);
        long totalTime = System.currentTimeMillis() - time;

        System.out.println("------------ 2. Multiply 2 Matrices (GPU-JOCL) ---------------");
        System.out.println("Result Matrix  = " + n + " x " + n);
        System.out.println("Elapsed Time = " + totalTime + "ms");
        System.out.println("-------------------------------------------------------");

        System.out.println();
        print(n, product);
        System.out.println("...");
    }

    private static void print(int n, float[] vector) {
        int size = n > 10 ? 10 : n;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(vector[row * n + col] + "    ");
            }
            System.out.println();
        }
    }

    private static void multiplyMatrices(int n, Pointer srcA, Pointer srcB, Pointer dst) {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);

        // Allocate the memory objects for the input- and output data
        cl_mem srcMemA = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * n * n, srcA, null);
        cl_mem srcMemB = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * n * n, srcB, null);
        cl_mem dstMem = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float * n * n, null, null);

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);

        // Set the arguments for the kernel
        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemA));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemB));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(dstMem));
        clSetKernelArg(kernel, a++, Sizeof.cl_uint, Pointer.to(new int[]{n}));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{n * n};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, null, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, dstMem, CL_TRUE, 0,
                n * n * Sizeof.cl_float, dst, 0, null, null);

        // Release kernel, program, and memory objects
        clReleaseMemObject(srcMemA);
        clReleaseMemObject(srcMemB);
        clReleaseMemObject(dstMem);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }
}
