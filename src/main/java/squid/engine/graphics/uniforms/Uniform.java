package squid.engine.graphics.uniforms;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.glGetUniformLocation;

public interface Uniform {

    Map<String, Integer> locations = new HashMap<>();

    String getName();
    int getLocation();
    void create(int programId) throws Exception;
    void set();

    static int createUniform(int programId, String name) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, name);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + name);
        }
        locations.put(name, uniformLocation);
        return uniformLocation;
    }

    default boolean exists() {
        return getLocation() >= 0;
    }
}
