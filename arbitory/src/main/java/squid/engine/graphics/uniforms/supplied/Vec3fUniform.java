package squid.engine.graphics.uniforms.supplied;

import org.joml.Vector3f;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glUniform3f;

public class Vec3fUniform extends SuppliedUniform<Vector3f> {

    public Vec3fUniform(String name) {
        super(name);
    }

    public Vec3fUniform(String name, Supplier<Vector3f> supplier) {
        super(name, supplier);
    }

    @Override
    public void set() {
        Vector3f vector = value.get();
        glUniform3f(this.location, vector.x, vector.y, vector.z);
    }
}
