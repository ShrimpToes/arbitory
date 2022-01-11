package squid.engine.graphics.uniforms.settable;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20C.glUniform3f;

public class SettableVec3fUniform extends SettableUniform<Vector3f> {
    public SettableVec3fUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        glUniform3f(this.location, value.x, value.y, value.z);
    }
}
