package squid.engine.graphics.uniforms.settable;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;

public class SettableMat4fArrayUniform extends SettableUniform<Matrix4f[]> {
    public SettableMat4fArrayUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = value != null ? value.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                value[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(location, false, fb);
        }
    }
}
