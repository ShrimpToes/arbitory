package squid.engine.scene.pieces;

import org.joml.Vector3f;
import squid.engine.graphics.Mesh;

public class GamePiece {

    private Mesh[] meshes;
    private final Vector3f position, rotation;
    private float scale;
    private int textPos;

    public GamePiece() {
        position = new Vector3f( 0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GamePiece(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GamePiece(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public void setMesh(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
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

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public int getTextPos() {
        return textPos;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public void cleanup() {
        if (meshes != null) {
            for (Mesh mesh : meshes) {
                mesh.cleanup();
            }
        }
    }
}
