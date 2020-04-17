package squid.engine.graphics.uniforms.supplied;

import squid.engine.graphics.uniforms.ValueUniform;

import java.util.function.Supplier;

public abstract class SuppliedUniform<T> extends ValueUniform<Supplier<T>> {

    public SuppliedUniform(String name) {
        super(name);
        this.name = name;
    }

    public SuppliedUniform(String name, Supplier<T> supplier) {
        this(name);
        this.value = supplier;
    }

    @Override
    public abstract void set();
}
