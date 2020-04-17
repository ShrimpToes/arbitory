package squid.engine.graphics.uniforms.settable;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;

public class SettableMat4fUniform extends SettableUniform<Matrix4f> {

    public SettableMat4fUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(location, false, fb);
        }
    }
}
