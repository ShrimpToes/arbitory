package squid.engine.graphics.uniforms;

import java.util.function.Supplier;

public abstract class ValueUniform<T> extends GenericUniform {

    protected T value;

    public ValueUniform(String name) {
        super(name);
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public abstract void set();
}
