package squid.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import squid.engine.Game;
import squid.engine.graphics.textures.Material;
import squid.engine.scene.*;
import squid.engine.IGame;
import squid.engine.graphics.*;
import squid.engine.Window;
import squid.engine.graphics.lighting.*;
import squid.engine.graphics.Mesh;
import squid.engine.graphics.textures.Texture;
import squid.engine.utils.Camera;
import squid.engine.utils.MouseInput;
import squid.engine.utils.OBJReader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGame {
    private float xPos = 0f;

    private float yPos = 0f;

    private float zPos = -3.05f;

    private Mesh mesh;

    private final Camera camera;

    private final Vector3f cameraInc;

    private static final float CAMERA_POS_STEP = 0.1f;

    private static final float MOUSE_SENSITIVITY = 0.5f;

    private final Renderer renderer;

    GamePiece[] gamePieces;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private float spotAngle = 0;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private PointLight[] pointLightList;

    private SpotLight spotLight;

    private SpotLight[] spotLightList;

    private Hud hud;

    private SkyBox skyBox;
    private Scene scene;
    private Lighting lighting;
    private Terrain terrain;
    private float angleInc;

    public DummyGame() {
        scene = new Scene();
        lighting = new Lighting();
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        List<GamePiece> gamePieceList = new ArrayList<>();

        hud = new Hud("demo");


        mesh = OBJReader.loadMesh("/models/cube.obj");
        mesh.setMaterial(new Material(new Texture("textures/grassblock.png"), 0.5f));

        Material rock = new Material();
        rock.setTexture(new Texture("textures/rock.png"));
        rock.setNormalMap(new Texture("textures/rock_normals.png"));

        Mesh rockMesh = OBJReader.loadMesh("/models/cube.obj");
        rockMesh.setMaterial(rock);
        GamePiece<Mesh> rockCube1 = new GamePiece<>(rockMesh);
        rockCube1.setScale(1f);
        rockCube1.setPosition(5f, 1f, 3f);
        GamePiece<Mesh> rockCube2 = new GamePiece<>(rockMesh);
        rockCube2.setScale(2f);
        rockCube2.setPosition(5f, 6f, 3f);

        skyBox = new SkyBox("/models/skybox.obj", "textures/skybox_texture.png", 10);

        GamePiece<Mesh> cube = new GamePiece<>(mesh);

//        terrain = new Terrain(2, 20, -0.1f, 0.1f, "textures/heightmap.png", "textures/terrain.png", 40);
        Mesh quadMesh = OBJReader.loadMesh("/models/plane.obj");
        Material quadMat = new Material(new Vector4f(0.0f, 0.0f, 1.0f, 10.0f), 1f);
        quadMesh.setMaterial(quadMat);
        GamePiece<Mesh> quad = new GamePiece<>(quadMesh);
        quad.setScale(10f);
        quad.setPosition(0f, -1.5f, 0f);

        setUpLights();

//        gamePieces = new GamePiece[]{cube, rockCube1, rockCube2, quad};
        gamePieces = new GamePiece[]{cube, quad};
        scene.setGamePieces(gamePieces);
//        scene.setGamePieces(terrain.getGamePieces());
        scene.setLighting(lighting);
        scene.setSkyBox(skyBox);

        scene.setSkyBox(skyBox);

        Fog fog = new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.15f);
        scene.setFog(fog);

        renderer.setDefaults(lighting, new Material(), fog);
        renderer.init();
    }

    private void setUpLights() {

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColor = new Vector3f(1f, 1f, 1f);
        Vector3f lightPosition = new Vector3f(0f, 0f, 1f);
        float lightIntensity = 1.0f;

        lightPosition = new Vector3f(2, 0.1f, 1);
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[]{pointLight};

        lightPosition = new Vector3f(0, 0.1f, 10f);
        pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, -0.05f, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        spotLightList = new SpotLight[]{spotLight, new SpotLight(spotLight)};

        lightPosition = new Vector3f(0, 1, 1);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
        directionalLight.setShadowPosMult(10);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);

        lighting.setAmbientLight(ambientLight);
        lighting.setPointLightList(pointLightList);
        lighting.setSpotLightList(spotLightList);
        lighting.setDirectionalLight(directionalLight);
        scene.setLighting(lighting);
        scene.setSkyBoxAmbientLight(new Vector3f(0.5f, 0.5f, 0.5f));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        angleInc = 0f;
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
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            angleInc -= 0.1f;
        }
        if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            angleInc += 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
//        updateDirectionalLight();
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
        if (camera.getPosition().y <= height) {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        lightAngle += angleInc;
        if ( lightAngle < 0 ) {
            lightAngle = 0;
        } else if (lightAngle > 180 ) {
            lightAngle = 180;
        }
        float zValue = (float)Math.cos(Math.toRadians(lightAngle));
        float yValue = (float)Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getLighting().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
        float lightAngle = (float)Math.toDegrees(Math.acos(lightDirection.z));
        hud.setStatusText("LightAngle: " + lightAngle);

        Mesh quadMesh = gamePieces[1].getMesh();
        quadMesh.setMaterial(new Material(renderer.getShadowMap().getDepthMap()));
        gamePieces[1].setMesh(quadMesh);
        scene.setGamePieces(gamePieces);
    }

    private void updateDirectionalLight() {
        // Update directional light direction, intensity and color
        lightAngle += 0.6f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) throws Exception {
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GamePiece gamePiece : gamePieces) {
            gamePiece.getMesh().cleanup();
        }
    }
}
