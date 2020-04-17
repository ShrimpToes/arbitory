package squid.engine.graphics.uniforms.supplied;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glUniform1f;

public class FloatUniform extends SuppliedUniform<Float> {

    public FloatUniform(String name) {
        super(name);
    }

    public FloatUniform(String name, Supplier<Float> supplier) {
        super(name, supplier);
    }

    @Override
    public void set() {
        glUniform1f(this.location, value.get());
    }
}
