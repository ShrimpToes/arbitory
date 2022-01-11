package squid.engine.graphics.uniforms;

public abstract class SettableArrayUniform<T> extends ValueUniform<T[]> {
    protected ValueUniform<T>[] uniforms;

    public SettableArrayUniform(String name) {
        super(name);
    }

    @Override
    public void create(int programId) throws Exception {
        for (Uniform uniform : uniforms) {
            uniform.create(programId);
        }
    }

    @Override
    public void setValue(T[] value) {
        for (int i = 0; i < (Math.min(value.length, uniforms.length)); i++) {
            uniforms[i].setValue(value[i]);
        }
    }

    @Override
    public void set() {
        for (Uniform uniform : uniforms) {
            uniform.set();
        }
    }
}
