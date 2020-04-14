package squid.engine.scene;

import org.joml.Vector3f;
import squid.engine.graphics.lighting.Lighting;
import squid.engine.graphics.Mesh;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.scene.pieces.particle.IParticleEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private SkyBox skyBox;

    private Vector3f skyBoxAmbientLight;

    private Lighting lighting;

    private Fog fog;

    private Map<Mesh, List<GamePiece>> meshMap;

    private IParticleEmitter[] particleEmitters;

    public Scene() {
        meshMap = new HashMap<>();
    }

    public void setGamePieces(GamePiece[] gamePieces) {
        if (gamePieces == null) return;
        for (GamePiece gamePiece : gamePieces) {
            Mesh mesh = gamePiece.getMesh();
            List<GamePiece> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
            if (!list.contains(gamePiece)) {
                list.add(gamePiece);
            }
        }
    }

    public Map<Mesh, List<GamePiece>> getMeshMap() {
        return meshMap;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public Vector3f getSkyBoxAmbientLight() {
        return skyBoxAmbientLight;
    }

    public void setSkyBoxAmbientLight(Vector3f skyBoxAmbientLight) {
        this.skyBoxAmbientLight = skyBoxAmbientLight;
    }

    public Lighting getLighting() {
        return lighting;
    }

    public void setLighting(Lighting lighting) {
        this.lighting = lighting;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public Fog getFog() {
        return fog;
    }
}
