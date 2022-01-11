package squid.engine.scene;

import org.joml.Vector3f;
import squid.engine.graphics.meshes.InstancedMesh;
import squid.engine.graphics.lighting.Lighting;
import squid.engine.graphics.meshes.Mesh;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.scene.pieces.GamePieceGroup;
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
    private Map<InstancedMesh, List<GamePiece>> instancedMeshMap;
    private IParticleEmitter[] particleEmitters;
    private World world;

    public Scene() {
        meshMap = new HashMap<>();
    }

    private Map<Mesh, List<GamePiece>> setGamePieces(GamePiece[] gamePieces) {
        if (gamePieces == null) return meshMap;
        for (GamePiece gamePiece : gamePieces) {
            if (gamePiece instanceof GamePieceGroup) {
                setGamePieces(((GamePieceGroup) gamePiece).getGamePieces());
            } else {
                Mesh mesh = gamePiece.getMesh();
                List<GamePiece> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
                if (!list.contains(gamePiece)) {
                    list.add(gamePiece);
                }
            }
        }
        return meshMap;
    }

    public Map<Mesh, List<GamePiece>> getMeshMap() {
        return meshMap;
    }

    public void setMeshMap(GamePiece[] gamePieces) {
        this.meshMap = setGamePieces(gamePieces);
    }

    public Map<InstancedMesh, List<GamePiece>> getInstancedMeshMap() {
        return instancedMeshMap;
    }

    public void setInstancedMeshMap(GamePiece[] gamePieces) {

    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
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
