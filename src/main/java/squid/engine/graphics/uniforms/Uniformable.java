package squid.engine.graphics.uniforms;

import squid.engine.graphics.uniforms.supplied.SuppliedUniform;

import java.util.function.Supplier;

public interface Uniformable {

    SuppliedUniform<?>[] getUniforms(String name);
    Supplier<?>[] getSuppliers();

}
