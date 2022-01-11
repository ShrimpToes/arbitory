package squid.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import squid.engine.IGame;
import squid.engine.Window;
import squid.engine.graphics.Renderer;
import squid.engine.graphics.lighting.DirectionalLight;
import squid.engine.graphics.lighting.Lighting;
import squid.engine.graphics.lighting.PointLight;
import squid.engine.graphics.lighting.SpotLight;
import squid.engine.graphics.meshes.Mesh;
import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.graphics.textures.Material;
import squid.engine.graphics.textures.Texture;
import squid.engine.scene.Fog;
import squid.engine.scene.Scene;
import squid.engine.scene.SkyBox;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.utils.Camera;
import squid.engine.utils.MouseInput;
import squid.engine.utils.readers.obj.OBJReader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class SpaceshipGame implements IGame {

    private Scene scene;
    private Lighting lighting;
    private Camera camera;

    private List<GamePiece> gamePieces;

    private final Vector3f cameraInc;

    private static final float CAMERA_POS_STEP = 0.1f;

    private static final float MOUSE_SENSITIVITY = 0.5f;


    private Renderer renderer;
    private Hud hud;

    private GamePiece spaceship;
    private GamePiece blender_cube;
    private GamePiece handmade_cube;
    private SkyBox skyBox;
    private Fog fog;

    public SpaceshipGame() {
        scene = new Scene();
        lighting = new Lighting();
        renderer = new Renderer();
        camera = new Camera();


        cameraInc = new Vector3f();
    }

    @Override
    public void init() throws Exception {
        scene = new Scene();
        lighting = new Lighting();

        hud = new Hud("text1", "text2");

        gamePieces = new ArrayList<>();

        /*

        Mesh spaceshipMesh = OBJReader.loadMesh("/models/spaceship0_poked.obj");
        spaceshipMesh.setMaterial(new Material(new Texture("textures/spaceship0.png"), 0.5f));
        spaceship = new GamePiece(spaceshipMesh);
        spaceship.setPosition(new Vector3f(2.0f, 0.0f, 0.0f));
        gamePieces.add(spaceship);

         */

        MeshBuilder.MeshBuffer spaceshipMeshBuffer = OBJReader.loadMeshBuffer("models/spaceship0_poked.obj");
        Mesh spaceshipMesh = spaceshipMeshBuffer.buildMesh();
        spaceshipMesh.setMaterial(new Material(new Texture("textures/spaceship0.png"), 0.5f));
        spaceship = new GamePiece(spaceshipMesh);
        spaceship.setPosition(new Vector3f(2.0f, 0.0f, 0.0f));
        gamePieces.add(spaceship);


        skyBox = new SkyBox("models/skybox.obj", "textures/skybox_texture.png", 10);



        Fog fog = new Fog(false, new Vector3f(0.1f, 0.1f, 0.1f), 0.0f);
        scene.setFog(fog);


        Vector3f lightPosition = new Vector3f(0, 1, 1);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        float lightIntensity = 1.0f;

        Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        DirectionalLight directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
        directionalLight.setShadowPosMult(15f);
        directionalLight.setOrthoCoords(camera.getPosition(), -1f, 25f);

        PointLight defaultPointLight = new PointLight(lightColor, lightPosition, 0.0f);


        SpotLight defaultSpotLight = new SpotLight(defaultPointLight, new Vector3f(0.0f, 0.0f, 1.0f), 90.0f);





        scene.setSkyBoxAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));


        lighting.setPointLightList(new PointLight[]{defaultPointLight});
        lighting.setSpotLightList(new SpotLight[]{defaultSpotLight});
        lighting.setAmbientLight(ambientLight);
        lighting.setDirectionalLight(directionalLight);
        scene.setLighting(lighting);

        scene.setSkyBox(skyBox);

        scene.setMeshMap(gamePieces.toArray(new GamePiece[0]));

        renderer.setDefaults(lighting, new Material(new Vector4f(0.3f, 0.3f, 0.3f, 0.0f), 0.3f), fog);

        renderer.init();
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W) ) {
            cameraInc.z = -1f;
        }
        if (window.isKeyPressed(GLFW_KEY_S) ) {
            cameraInc.z = 1f;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1f;
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1f;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1f;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
//        float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
//        if (camera.getPosition().y <= height) {
//            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
//        }

        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        hud.setStatusText(camera.getPosition().toString(), camera.getRotation().toString());
    }

    @Override
    public void render(Window window) throws Exception {
        renderer.render(camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();

//        spaceship.cleanup();
        for (GamePiece gamePiece : gamePieces) {
            gamePiece.cleanup();
        }
    }
}
