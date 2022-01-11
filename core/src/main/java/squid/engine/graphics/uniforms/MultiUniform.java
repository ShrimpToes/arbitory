package squid.engine.graphics.uniforms;

import squid.engine.graphics.uniforms.supplied.SuppliedUniform;

public class MultiUniform<T extends Uniformable> extends ValueUniform<T> {

    private SuppliedUniform[] uniforms;

    public MultiUniform(String name, T defaultValue) {
        super(name);
        this.location = 0;
        this.value = defaultValue;
    }

    @Override
    public void create(int programId) throws Exception {
        uniforms = value.getUniforms(name);
        for (SuppliedUniform<?> uniform : uniforms) {
            uniform.create(programId);
        }
    }

    public void setSuppliers() {
        int i = 0;
        for (SuppliedUniform uniform : uniforms) {
            uniform.setValue(value.getSuppliers()[i]);
            i++;
        }
    }

    @Override
    public void set() {
        setSuppliers();
        for (SuppliedUniform<?> uniform : uniforms) {
            uniform.set();
        }
    }

    @Override
    public boolean exists() {
        for (SuppliedUniform<?> uniform : uniforms) {
            if (!uniform.exists()) {
                return false;
            }
        }
        return true;
    }
}
