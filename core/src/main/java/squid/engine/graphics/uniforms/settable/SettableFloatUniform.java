package squid.engine.graphics.uniforms.settable;

import static org.lwjgl.opengl.GL20C.glUniform1f;

public class SettableFloatUniform extends SettableUniform<Float> {
    public SettableFloatUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        glUniform1f(location, value);
    }
}
