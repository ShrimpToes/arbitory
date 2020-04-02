package squid.engine.graphics.textures;

import org.joml.Vector4f;
import squid.engine.graphics.textures.Texture;
import squid.engine.graphics.uniforms.*;
import squid.engine.graphics.uniforms.supplied.FloatUniform;
import squid.engine.graphics.uniforms.supplied.IntegerUniform;
import squid.engine.graphics.uniforms.supplied.SuppliedUniform;
import squid.engine.graphics.uniforms.supplied.Vec4fUniform;

import java.util.function.Supplier;

public class Material implements Uniformable {
    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f ambientColour;

    private Vector4f diffuseColour;

    private Vector4f specularColour;

    private float reflectance;

    private Texture texture;

    private Texture normalMap;

    public Material() {
        this.ambientColour = DEFAULT_COLOUR;
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }

    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, colour, null, reflectance);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public boolean hasNormalMap() {
        return normalMap != null;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public SuppliedUniform<?>[] getUniforms(String name) {
        return new SuppliedUniform[]{
                new Vec4fUniform(name + ".ambient"),
                new Vec4fUniform(name + ".diffuse"),
                new Vec4fUniform(name + ".specular"),
                new IntegerUniform(name + ".hasTexture"),
                new IntegerUniform(name + ".hasNormalMap"),
                new FloatUniform(name + ".reflectance")
        };
    }

    @Override
    public Supplier<?>[] getSuppliers() {
        return new Supplier[]{
                this::getAmbientColour,
                this::getDiffuseColour,
                this::getSpecularColour,
                () -> this.isTextured() ? 1 : 0,
                ()  -> this.hasNormalMap() ? 1 : 0,
                this::getReflectance
        };
    }
}
