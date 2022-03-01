package squid.engine.graphics.uniforms.settable.physics;

import org.joml.Vector3f;

public class BoundingBox {

    public float x, y, z, width, height, length;
    public Vector3f[] points;

    public BoundingBox(float x, float y, float z, float width, float height, float length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width; //x length
        this.height = height; //y length
        this.length = length; //z length

        points = new Vector3f[]{new Vector3f(x, y, z), new Vector3f(x + width, y, z),
                new Vector3f(x + width, y + height, z),
                new Vector3f(x + width, y + height, z + length),
                new Vector3f(x, y + height, z), new Vector3f(x, y + height, z + length),
                new Vector3f(x, y, z + length), new Vector3f(x + width, y, z + length)};
    }

    public boolean collides(Vector3f point) {
        return (point.x >= x && point.x <= x + width) &&
                (point.y > y && point.y <= y + height) &&
                (point.z >= z && point.z <= z + length);
    }

    public boolean collides(BoundingBox box) {
        boolean out = true;
        for (Vector3f point : box.points) {
            if (collides(point)) { out = false; }
        }
        return out;
    }
}
