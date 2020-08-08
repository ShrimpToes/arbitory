package squid.engine.scene;

import org.joml.Vector3f;
import squid.engine.graphics.uniforms.Uniformable;
import squid.engine.graphics.uniforms.supplied.FloatUniform;
import squid.engine.graphics.uniforms.supplied.IntegerUniform;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;
import squid.engine.graphics.uniforms.supplied.Vec3fUniform;

import java.util.function.Supplier;

public class Fog implements Uniformable {

    private boolean active;

    private Vector3f colour;

    private float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.colour = new Vector3f(0, 0, 0);
        this.density = 0;
    }

    public Fog(boolean active, Vector3f colour, float density) {
        this.colour = colour;
        this.density = density;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public int getActive() {
        return active ? 1 : 0;
    }

    public Vector3f getColour() {
        return colour;
    }

    public float getDensity() {
        return density;
    }

    @Override
    public SuppliedUniform<?>[] getUniforms(String name) {
        return new SuppliedUniform[]{
                new IntegerUniform(name + ".activeFog"),
                new Vec3fUniform(name + ".color"),
                new FloatUniform(name + ".density")
        };
    }

    @Override
    public Supplier<?>[] getSuppliers() {
        return new Supplier[]{
                this::getActive,
                this::getColour,
                this::getDensity
        };
    }
}
