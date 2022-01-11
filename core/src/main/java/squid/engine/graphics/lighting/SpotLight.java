package squid.engine.graphics.lighting;

import org.joml.Vector3f;
import squid.engine.graphics.uniforms.supplied.FloatUniform;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;
import squid.engine.graphics.uniforms.Uniformable;
import squid.engine.graphics.uniforms.supplied.Vec3fUniform;
import squid.engine.utils.Utils;

import java.util.function.Supplier;

public class SpotLight implements Uniformable {
    private PointLight pointLight;

    private Vector3f coneDirection;

    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.getPointLight()),
                new Vector3f(spotLight.getConeDirection()),
                0);
        setCutOff(spotLight.getCutOff());
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public final void setCutOffAngle(float cutOffAngle) {
        this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle)));
    }

    @Override
    public SuppliedUniform<?>[] getUniforms(String name) {
        SuppliedUniform<?>[] uniforms = new SuppliedUniform<?>[]{
                new Vec3fUniform(name + ".conedir"),
                new FloatUniform(name + ".cutoff")
        };

        return Utils.combineArrays(SuppliedUniform.class, pointLight.getUniforms(name + ".pl"), uniforms);
    }

    @Override
    public Supplier<?>[] getSuppliers() {
        Supplier<?>[] suppliers = new Supplier<?>[]{
                this::getConeDirection,
                this::getCutOff
        };
        return Utils.combineArrays(Supplier.class, pointLight.getSuppliers(), suppliers);
    }
}
