package squid.engine.utils.readers;

import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.utils.Utils;

import java.util.List;

public class IGPReader {

    public static MeshBuilder.MeshBuffer readIGP(String file, int instances, MeshBuilder.MeshBuffer buffer) throws Exception {
        List<String> lines = Utils.readAllLines(file);

        try {
            int numItems = Integer.parseInt(lines.get(0));
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }

        for (String line : lines) {


        }
        return null;
    }
}
