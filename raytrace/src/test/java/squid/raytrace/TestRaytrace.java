package squid.raytrace;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TestRaytrace {

    @Test
    public void testRaytrace() {
        Raytrace rt = new Raytrace(1200, 800, 100);
        int[] img = rt.raytrace();
        BufferedImage buf = new BufferedImage(rt.getWidth(), rt.getHeight(), BufferedImage.TYPE_INT_RGB);
        buf.setRGB(0, 0, rt.getWidth(), rt.getHeight(), img, 0, rt.getWidth());
        try {
            System.out.println("Created image at: " + Paths.get("").toAbsolutePath());
            ImageIO.write(buf, "png", new File(Paths.get("").toAbsolutePath() + "\\image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        rt.free();

    }
}
