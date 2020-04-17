package squid.engine.graphics.uniforms.settable;

import static org.lwjgl.opengl.GL20C.glUniform1i;

public class SettableIntegerUniform extends SettableUniform<Integer> {

    public SettableIntegerUniform(String name) {
        super(name);
    }

    @Override
    public void set() {
        glUniform1i(location, value);
    }
}
