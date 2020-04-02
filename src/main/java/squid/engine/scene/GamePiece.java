package squid.engine.scene;

import org.joml.Vector3f;
import squid.engine.graphics.Mesh;

public class GamePiece<T extends Mesh> {

    private T mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    public GamePiece() {
        position = new Vector3f( 0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GamePiece(T mesh) {
        this();
        this.setMesh(mesh);
    }

    public void setMesh(T mesh) {
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public T getMesh() {
        return mesh;
    }
}
