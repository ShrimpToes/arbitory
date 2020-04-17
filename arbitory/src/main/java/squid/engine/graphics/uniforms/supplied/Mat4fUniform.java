package squid.engine.graphics.uniforms.supplied;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;

import java.nio.FloatBuffer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;

public class Mat4fUniform extends SuppliedUniform<Matrix4f> {

    public Mat4fUniform(String name) {
        super(name);
    }

    public Mat4fUniform(String name, Supplier<Matrix4f> supplier) {
         super(name, supplier);
    }

    @Override
    public void set() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get().get(fb);
            glUniformMatrix4fv(location, false, fb);
        }
    }
}
