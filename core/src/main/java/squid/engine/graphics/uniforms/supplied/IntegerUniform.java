package squid.engine.graphics.uniforms.supplied;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glUniform1i;

public class IntegerUniform extends SuppliedUniform<Integer> {

    public IntegerUniform(String name) {
        super(name);
    }

    public IntegerUniform(String name, Supplier<Integer> supplier) {
        super(name, supplier);
    }

    @Override
    public void set() {
        glUniform1i(location, value.get());
    }
}
