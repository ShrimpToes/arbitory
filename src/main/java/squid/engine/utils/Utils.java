package squid.engine.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static <T> T[] fillToSize(Class<T> type , T[] input, int length) {
        @SuppressWarnings("unchecked")
        T[] output =(T[])Array.newInstance(type, length);
        if (input.length < length) {
            int i = length - input.length;
            for (int e = 0; e < length; e++) {
                if (e < input.length) {
                    output[e] = input[e];
                } else {
                    output[e] = input[0];
                }
            }
        }
        return output;
    }

    public static <T> T[] combineArrays(Class<T> type , T[] array1, T[] array2) {
        int finalLength = array1.length + array2.length;
        @SuppressWarnings("unchecked")
        T[] output = (T[]) Array.newInstance(type, finalLength);
        for (int i = 0; i < finalLength; i++) {
            if (i < array1.length) {
                output[i] = array1[i];
            } else {
                output[i] = array2[i - array1.length];
            }
        }
        return output;
    }
}
