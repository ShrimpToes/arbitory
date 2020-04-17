package squid.engine.scene.pieces.animated;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class AnimatedVertex {
    public Vector3f position, normal;
    public Vector2f textCoords;
    public float[] weights;
    public int[] jointIndices;

    public AnimatedVertex() {
        super();
        normal = new Vector3f();
    }
}
