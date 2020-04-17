package squid.engine.graphics.uniforms;

public class ListUniform<T extends Uniformable> extends ValueUniform<MultiUniform<T>[]> {

    protected int maxSize;
    protected int currSize;

    public ListUniform(String name, int maxSize, T[] defaults) {
        super(name);
        this.location = 0;
        value = new MultiUniform[maxSize];
        this.maxSize = maxSize;
        for (int i = 0; i < maxSize; i++) {
            value[i] = (new MultiUniform<>(name + "[" + i + "]", defaults[i]));
        }
    }

    public void setSuppliers() {
        for (int i = 0; i < currSize; i++) {
            value[i].setSuppliers();
        }
    }

    private void setValue(int index, T value) {
        this.value[index].setValue(value);
    }

    public void setValue(T[] values) {
        this.currSize = values.length;
        int i = 0;
        for (T value : values) {
            setValue(i, value);
            i++;
        }
    }

    @Override
    public void create(int programId) throws Exception {
        for (Uniform uniform : value) {
            uniform.create(programId);
        }
    }

    @Override
    public void set() {
        setSuppliers();
        for (int i = 0; i < currSize; i++) {
            value[i].set();
        }
    }

    @Override
    public boolean exists() {
        for (Uniform uniform : value) {
            if(!uniform.exists()) {
                return false;
            }
        }
        return true;
    }

}
