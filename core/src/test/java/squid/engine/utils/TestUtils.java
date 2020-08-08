package squid.engine.utils;

import org.junit.Test;
import squid.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestUtils {

    @Test
    public void testListToArray() {
        List<Float> list = new ArrayList<>();
        list.add(1f);
        list.add(2f);
        list.add(3f);
        list.add(4f);
        float[] array = new float[]{
                1f, 2f, 3f, 4f
        };
        float[] testArray = Utils.listToArray(list);
        int i = 0;
        for (float value : testArray) {
            assertEquals(list.get(i), value, 1e-15);
            i++;
        }
    }

    @Test
    public void testFillToSize() {
        Float[] floats = new Float[]{
                0F, 3F, 5F
        };
        Float[] moreFloats = Utils.fillToSize(Float.class, floats, 10);
        Float[] array = new Float[]{
                0F, 3F, 5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F
        };
        int i = 0;
        for (Float value : moreFloats) {
//            System.out.println(value + " supposed to be : " + array[i]);
            assertEquals(array[i], value);
            i++;
        }
    }

    @Test
    public void testCombineArrays() {
        Float[] floats1 = new Float[]{
                0F, 1F, 2F
        };
        Float[] floats2 = new Float[]{
                3F, 4F, 5F, 6F
        };
        Float[] endFloats = new Float[] {
                0F, 1F, 2F, 3F, 4F, 5F, 6F
        };
        Float[] test = Utils.combineArrays(Float.class , floats1, floats2);
        int i =0;
        for (Float value : test) {
//            System.out.println(value + " supposed to be: " + endFloats[i]);
            assertEquals(value, endFloats[i], 1e-15);
            i++;
        }
    }
}
