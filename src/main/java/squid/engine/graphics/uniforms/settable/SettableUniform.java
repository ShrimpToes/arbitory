package squid.engine.graphics.uniforms.settable;

import squid.engine.graphics.uniforms.ValueUniform;

public abstract class SettableUniform<T> extends ValueUniform<T> {

    public SettableUniform(String name) {
        super(name);
    }

    @Override
    public abstract void set();
}
