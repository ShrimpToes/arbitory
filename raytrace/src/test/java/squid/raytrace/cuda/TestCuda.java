package squid.raytrace.cuda;

import org.junit.jupiter.api.Test;
import org.lwjgl.PointerBuffer;
import org.lwjgl.cuda.CUDA;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.cuda.CU.*;
import static org.lwjgl.cuda.NVRTC.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TestCuda {

    private static final int ARRAY_SIZE = 1000000;

    private static final String KERNEL_CU =
            "#define N " + ARRAY_SIZE + "\n" +
                    "\n" +
                    "extern \"C\" __global__ void matSum(int *a, int *b, int *c)\n" +
                    "{\n" +
                    "    int tid = blockIdx.x;\n" +
                    "    if (tid < N)\n" +
                    "        c[tid] = a[tid] + b[tid];\n" +
                    "}\n";

    private static final String KERNEL_NAME = "matSum";

    private static long ctx;

    @Test
    public void helloCuda() {
        ByteBuffer PTX;

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer major = stack.mallocInt(1);
            IntBuffer minor = stack.mallocInt(1);

            checkNVRTC(nvrtcVersion(major, minor));

            System.out.println("Compiling kernel with NVRTC v" + major.get(0) + "." + minor.get(0));

            PointerBuffer pp = stack.mallocPointer(1);

            checkNVRTC(nvrtcCreateProgram(pp, KERNEL_CU, "matSum.cu", null, null));

            long program = pp.get();

            int compilationStatus = nvrtcCompileProgram(program, null);
            {
                checkNVRTC(nvrtcGetProgramLogSize(program, pp.clear()));
                if (1L < pp.get()) {
                    ByteBuffer log = stack.malloc((int)pp.get());

                    checkNVRTC(nvrtcGetProgramLog(program, log));
                    System.err.println("Compilation Log: ");
                    System.err.println("-----------------");
                    System.err.println(memASCII(log));
                }
            }

            checkNVRTC(compilationStatus);

            checkNVRTC(nvrtcGetPTXSize(program, pp.clear()));
            PTX = memAlloc((int)pp.get(0));
            checkNVRTC(nvrtcGetPTX(program, PTX));

            System.out.println("\nCompiled PTX: ");
            System.out.println("----------------");
            System.out.println(memASCII(PTX));
        }

        IntBuffer hostA = memAllocInt(ARRAY_SIZE);
        IntBuffer hostB = memAllocInt(ARRAY_SIZE);
        IntBuffer hostC = memAllocInt(ARRAY_SIZE);

        long time = System.nanoTime();

        for (int i = 0; i < ARRAY_SIZE; ++i) {
            hostA.put(i, ARRAY_SIZE - i);
            hostB.put(i, i * i);
        }

        System.out.println("- Arrays created in " + (System.nanoTime() - time) / 1000000d + " milliseconds.");

        long
                deviceA,
                deviceB,
                deviceC;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);
            IntBuffer pi = stack.mallocInt(1);

            if (CUDA.isPerThreadDefaultStreamSupported()) {
                Configuration.CUDA_API_PER_THREAD_DEFAULT_STREAM.set(true);
            }

            System.out.format("- Initializing...\n");
            check(cuInit(0));

            check(cuDeviceGetCount(pi));
            if (pi.get() == 0) {
                throw new IllegalStateException("Error: No Devices Supporting CUDA!");
            }

            //get first CUDA device
            check(cuDeviceGet(pi.clear(), 0));
            int device = pi.get();

            ByteBuffer pb = stack.malloc(100);
            check(cuDeviceGetName(pb, device));
            System.out.format("> Using Device 0: %s\n", memASCII(pb));

            IntBuffer minor = stack.mallocInt(1);
            check(cuDeviceComputeCapability(pi.clear(), minor, device));
            System.out.format("> GPU Device has SM %d.%d compute capability", pi.get(0), minor.get(0));

            check(cuDeviceTotalMem(pp, device));
            System.out.format(" Total amount of global memory:   %d bytes\n", pp.get(0));
            System.out.format(" 64-bit Memory Address:           %s\n", (pp.get(0) > 4 * 1024 * 1024 * 1024L) ? "YES" : "NO");

            check(cuCtxCreate(pp, 0, device));
            ctx = pp.get(0);

            time = System.nanoTime();

            check(cuModuleLoadData(pp, PTX));
            long module = pp.get(0);

            check(cuModuleGetFunction(pp, module, KERNEL_NAME));
            long function = pp.get(0);

            check(cuMemAlloc(pp, Integer.BYTES * ARRAY_SIZE));
            deviceA = pp.get(0);

            check(cuMemAlloc(pp, Integer.BYTES * ARRAY_SIZE));
            deviceB = pp.get(0);

            check(cuMemAlloc(pp, Integer.BYTES * ARRAY_SIZE));
            deviceC = pp.get(0);

            check(cuMemcpyHtoD(deviceA, hostA));
            check(cuMemcpyHtoD(deviceB, hostB));

            System.out.format("# Running the Kernel...\n");

            // grid for kernel: <<<N, 1>>>
            check(cuLaunchKernel(
                    function, ARRAY_SIZE,
                    1, 1,
                    1, 1, 1,
                    0, 0,
                    // method 1: unpacked (simple, no alignment requirements)
                    stack.pointers(
                            memAddress(stack.longs(deviceA)),
                            memAddress(stack.longs(deviceB)),
                            memAddress(stack.longs(deviceC))
                    ),
                    null
                    /*,
                // method 2: packed (user is responsible for correct argument alignment)
                stack.pointers(
                    CU_LAUNCH_PARAM_BUFFER_POINTER, memAddress(stack.longs(
                        deviceA,
                        deviceB,
                        deviceC
                    )),
                    CU_LAUNCH_PARAM_BUFFER_SIZE, memAddress(stack.pointers(3 * Long.BYTES)),
                    CU_LAUNCH_PARAM_END
                )*/));

            System.out.println("# Kernel Complete in " + (System.nanoTime() - time) / 1000000d + " milliseconds.");

            check(cuMemcpyDtoH(hostC, deviceC));

            time = System.nanoTime();

            for (int i = 0; i < ARRAY_SIZE; ++i) {
                if (hostC.get(i) != hostA.get(i) + hostB.get(i)) {
                    System.out.format(
                            "* Error at array position %d: Expected %d, got %d\n",
                            i, hostA.get(i) + hostB.get(i), hostC.get(i));
                }
            }

            System.out.println("* Arrays checked in " + (System.nanoTime() - time) / 1000000d + " milliseconds.");

            System.out.format("*** All checks Complete.\n");

            System.out.format("- Finalizing...\n");
            check(cuMemFree(deviceA));
            check(cuMemFree(deviceB));
            check(cuMemFree(deviceC));
            check(cuCtxDetach(ctx));
        }
    }

    private static void checkNVRTC(int err) {
        if (err != NVRTC_SUCCESS) {
            throw new IllegalStateException(nvrtcGetErrorString(err));
        }
    }

    private static void check(int err) {
        if (err != CUDA_SUCCESS) {
            if (ctx != NULL) {
                cuCtxDetach(ctx);
                ctx = NULL;
            }
            throw new IllegalStateException(Integer.toString(err));
        }
    }

}
