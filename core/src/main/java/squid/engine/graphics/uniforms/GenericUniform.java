package squid.engine.graphics.uniforms;

public abstract class GenericUniform implements Uniform {

    protected String name;
    protected int location = -1;

    public GenericUniform(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLocation() {
        return location;
    }

    @Override
    public void create(int programId) throws Exception {
        location = Uniform.createUniform(programId, name);
    }

    @Override
    public abstract void set();
}
