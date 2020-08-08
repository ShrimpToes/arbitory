package squid.engine.graphics.lighting;

import org.joml.Vector3f;
import squid.engine.graphics.uniforms.supplied.FloatUniform;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;
import squid.engine.graphics.uniforms.Uniformable;
import squid.engine.graphics.uniforms.supplied.Vec3fUniform;

import java.util.function.Supplier;

public class DirectionalLight implements Uniformable {
    private Vector3f color;

    private transient Vector3f direction;

    private OrthoCoords orthoCoords;

    private float intensity;

    private float shadowPosMult;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.orthoCoords = new OrthoCoords();
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
        shadowPosMult = 1;
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
        this.orthoCoords = light.getOrthoCoords();
        this.color = light.getColor();
        this.shadowPosMult = light.getShadowPosMult();
    }

    public float getShadowPosMult() {
        return shadowPosMult;
    }

    public void setShadowPosMult(float shadowPosMult) {
        this.shadowPosMult = shadowPosMult;
    }

    public OrthoCoords getOrthoCoords(){
        return orthoCoords;
    }

    public void setOrthoCords(float left, float right, float bottom, float top, float near, float far) {
        orthoCoords.left = left;
        orthoCoords.right = right;
        orthoCoords.bottom = bottom;
        orthoCoords.top = top;
        orthoCoords.near = near;
        orthoCoords.far = far;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public SuppliedUniform<?>[] getUniforms(String name) {
        return new SuppliedUniform[]{
                new Vec3fUniform(name + ".color"),
                new Vec3fUniform(name + ".direction"),
                new FloatUniform(name + ".intensity")
        };
    }

    @Override
    public Supplier<?>[] getSuppliers() {
        return new Supplier[]{
                this::getColor,
                this::getDirection,
                this::getIntensity
        };
    }

    public static class OrthoCoords {

        public float left;

        public float right;

        public float bottom;

        public float top;

        public float near;

        public float far;
    }
}
