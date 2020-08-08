package squid.engine.graphics.uniforms.settable;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL20C.glUniform4f;

public class SettableVec4fUniform extends SettableUniform<Vector4f> {

    public SettableVec4fUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        glUniform4f(this.location, value.x, value.y, value.z, value.w);
    }
}
