package squid.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import squid.engine.graphics.lighting.*;
import squid.engine.graphics.textures.Material;
import squid.engine.graphics.textures.Texture;
import squid.engine.graphics.uniforms.*;
import squid.engine.graphics.uniforms.settable.*;
import squid.engine.scene.Fog;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.IHud;
import squid.engine.scene.Scene;
import squid.engine.scene.SkyBox;
import squid.engine.scene.pieces.animated.AnimGamePiece;
import squid.engine.scene.pieces.animated.AnimatedFrame;
import squid.engine.scene.pieces.particle.IParticleEmitter;
import squid.engine.utils.Camera;
import squid.engine.utils.Transformation;
import squid.engine.utils.Utils;
import squid.engine.Window;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private Shader sceneShader;
    private Shader hudShader;
    private Shader skyBoxShader;
    private Shader depthShader;
    private Shader particleShader;

    private Transformation transformation;
    private ShadowMap shadowMap;

    private float specularPower;

    private DirectionalLight defaultDirLight;
    private PointLight[] defaultPointLights;
    private SpotLight[] defaultSpotLights;
    private Material defaultMaterial;
    private Fog defaultFog;

    private ListUniform<PointLight> pointLights;
    private ListUniform<SpotLight> spotLights;
    private MultiUniform<DirectionalLight> directionalLight;
    private SettableMat4fUniform projectionMatrix;
    private SettableMat4fUniform orthoProjectionMatrix;
    private SettableMat4fUniform modelViewNonInstancedMatrix;
    private SettableIntegerUniform textureSampler;
    private SettableIntegerUniform normalMap;
    private MultiUniform<Material> material;
    private SettableFloatUniform specularPowerUniform;
    private SettableVec3fUniform ambientLightUniform;
    private MultiUniform<Fog> fogUniform;
    private SettableIntegerUniform shadowMapUniform;
    private SettableMat4fUniform modelLightViewNonInstancedMatrix;
    private SettableMat4fArrayUniform jointsMatrix;
    private SettableIntegerUniform isInstanced;

    private SettableMat4fUniform skyBoxProjectionMatrix;
    private SettableIntegerUniform skyBoxTextureSampler;
    private SettableMat4fUniform skyBoxModelViewMatrix;
    private SettableVec3fUniform skyBoxAmbientLightUniform;

    private SettableVec4fUniform color;
    private SettableMat4fUniform projModelMatrix;

    private SettableMat4fArrayUniform depthJointsMatrices;
    private SettableMat4fUniform depthOrthoProjectionMatrix;
    private SettableMat4fUniform depthModelLightViewMatrix;

    private SettableIntegerUniform particleTextureSampler;
    private SettableMat4fUniform particleProjectionMatrix;
    private SettableIntegerUniform particleNumRows;
    private SettableIntegerUniform particleNumCols;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init() throws Exception{
        shadowMap = new ShadowMap();
        setUpUniforms();
        setUpSceneShader();
        setupHudShader();
        setupSkyBoxShader();
        setUpDepthShader();
        setUpParticleShader();
    }

    public void setDefaults(Lighting lighting, Material material, Fog fog) {
        this.defaultPointLights = Utils.fillToSize(PointLight.class,  lighting.getPointLightList(), MAX_POINT_LIGHTS);
        this.defaultSpotLights = Utils.fillToSize(SpotLight.class, lighting.getSpotLightList(), MAX_SPOT_LIGHTS);
        this.defaultDirLight = lighting.getDirectionalLight();
        this.defaultMaterial = material;
        this.defaultFog = fog;
    }

    private void setUpUniforms() {
        pointLights = new ListUniform<>("pointLights", MAX_POINT_LIGHTS, defaultPointLights);
        spotLights = new ListUniform<>("spotLights", MAX_SPOT_LIGHTS, defaultSpotLights);
        directionalLight = new MultiUniform<>("directionalLight", defaultDirLight);
        projectionMatrix = new SettableMat4fUniform("projectionMatrix");
        orthoProjectionMatrix = new SettableMat4fUniform("orthoProjectionMatrix");
        modelViewNonInstancedMatrix = new SettableMat4fUniform("modelViewNonInstancedMatrix");
        textureSampler = new SettableIntegerUniform("texture_sampler");
        normalMap = new SettableIntegerUniform("normalMap");
        material = new MultiUniform<>("material", defaultMaterial);
        specularPowerUniform = new SettableFloatUniform("specularPower");
        ambientLightUniform = new SettableVec3fUniform("ambientLight");
        fogUniform = new MultiUniform<>("fog", defaultFog);
        shadowMapUniform = new SettableIntegerUniform("shadowMap");
        modelLightViewNonInstancedMatrix = new SettableMat4fUniform("modelLightViewNonInstancedMatrix");
        jointsMatrix = new SettableMat4fArrayUniform("jointsMatrix");
        isInstanced = new SettableIntegerUniform("isInstanced");

        skyBoxProjectionMatrix = new SettableMat4fUniform("projectionMatrix");
        skyBoxModelViewMatrix = new SettableMat4fUniform("modelViewMatrix");
        skyBoxTextureSampler = new SettableIntegerUniform("texture_sampler");
        skyBoxAmbientLightUniform = new SettableVec3fUniform("ambientLight");

        color = new SettableVec4fUniform("color");
        projModelMatrix = new SettableMat4fUniform("projModelMatrix");

        depthJointsMatrices = new SettableMat4fArrayUniform("jointsMatrix");
        depthOrthoProjectionMatrix = new SettableMat4fUniform("orthoProjectionMatrix");
        depthModelLightViewMatrix = new SettableMat4fUniform("modelLightViewMatrix");

        particleTextureSampler = new SettableIntegerUniform("texture_sampler");
        particleProjectionMatrix = new SettableMat4fUniform("projectionMatrix");
        particleNumRows = new SettableIntegerUniform("numRows");
        particleNumCols = new SettableIntegerUniform("numCols");

        textureSampler.setValue(0);
        normalMap.setValue(1);
        shadowMapUniform.setValue(2);
        skyBoxTextureSampler.setValue(0);
        particleTextureSampler.setValue(0);
        specularPowerUniform.setValue(specularPower);
    }

    private void createSceneUniforms(int programId) throws Exception {
        pointLights.create(programId);
        spotLights.create(programId);
        directionalLight.create(programId);
        projectionMatrix.create(programId);
        orthoProjectionMatrix.create(programId);
        modelViewNonInstancedMatrix.create(programId);
        modelLightViewNonInstancedMatrix.create(programId);
        textureSampler.create(programId);
        normalMap.create(programId);
        shadowMapUniform.create(programId);
        material.create(programId);
        specularPowerUniform.create(programId);
        ambientLightUniform.create(programId);
        fogUniform.create(programId);
        jointsMatrix.create(programId);
    }

    private void createSkyBoxUniforms(int programId) throws Exception {
        skyBoxProjectionMatrix.create(programId);
        skyBoxTextureSampler.create(programId);
        skyBoxModelViewMatrix.create(programId);
        skyBoxAmbientLightUniform.create(programId);
    }

    private void createHudUniforms(int programId) throws Exception {
        color.create(programId);
        projModelMatrix.create(programId);
    }

    private void createDepthUniforms(int programId) throws Exception {
        depthJointsMatrices.create(programId);
        depthOrthoProjectionMatrix.create(programId);
        depthModelLightViewMatrix.create(programId);
    }

    private void createParticleUniforms(int programId) throws Exception {
        particleTextureSampler.create(programId);
        particleProjectionMatrix.create(programId);
        particleNumRows.create(programId);
        particleNumCols.create(programId);
    }

    public void setUpSceneShader() throws Exception {
        sceneShader = new Shader();
        sceneShader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        sceneShader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        sceneShader.link();
        createSceneUniforms(sceneShader.getProgramId());
    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShader = new Shader();
        skyBoxShader.createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
        skyBoxShader.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
        skyBoxShader.link();
        createSkyBoxUniforms(skyBoxShader.getProgramId());
    }

    private void setupHudShader() throws Exception {
        hudShader = new Shader();
        hudShader.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
        hudShader.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
        hudShader.link();
        createHudUniforms(hudShader.getProgramId());
    }

    private void setUpDepthShader() throws Exception {
        depthShader = new Shader();
        depthShader.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
        depthShader.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));
        depthShader.link();
        createDepthUniforms(depthShader.getProgramId());
    }

    private void setUpParticleShader() throws Exception {
        particleShader = new Shader();
        particleShader.createVertexShader(Utils.loadResource("/shaders/particle_vertex.vs"));
        particleShader.createFragmentShader(Utils.loadResource("/shaders/particle_fragment.fs"));
        particleShader.link();
        createParticleUniforms(particleShader.getProgramId());
    }

    public void render(Window window, Camera camera, Scene scene, IHud hud) throws Exception {
        clear();

        renderDepthMap(window, scene, camera);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(scene);
        renderParticles(scene);
//        renderSkyBox(scene);
        renderHud(window, hud);
    }

    public void renderDepthMap(Window window, Scene scene, Camera camera) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        DirectionalLight light = scene.getLighting().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection)
                .mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));

        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right,
                orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthOrthoProjectionMatrix.setValue(orthoProjMatrix);
        depthOrthoProjectionMatrix.set();

        Map<Mesh, List<GamePiece>> mapMeshes = scene.getMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GamePiece gamePiece) -> {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gamePiece, lightViewMatrix);
                        this.depthModelLightViewMatrix.setValue(modelLightViewMatrix);
                        this.depthModelLightViewMatrix.set();
                        if (gamePiece instanceof AnimGamePiece) {
                            AnimGamePiece animGamePiece = (AnimGamePiece) gamePiece;
                            AnimatedFrame frame = animGamePiece.getCurrentFrame();
                            depthJointsMatrices.setValue(frame.getJointMatrices());
                            depthJointsMatrices.set();
                        }
                    }
            );
        }

        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void renderScene(Scene scene) {

        sceneShader.bind();

        projectionMatrix.setValue(transformation.getProjectionMatrix());
        projectionMatrix.set();
        orthoProjectionMatrix.setValue(transformation.getOrthoProjectionMatrix());
        orthoProjectionMatrix.set();
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = transformation.getViewMatrix();
        renderLights(viewMatrix, scene.getLighting());

        fogUniform.setValue(scene.getFog());
        fogUniform.set();
        textureSampler.set();
        normalMap.set();
        shadowMapUniform.set();

        renderMeshes(scene, false, sceneShader, viewMatrix, lightViewMatrix);
        renderInstancedMeshes(scene, false, sceneShader, viewMatrix, lightViewMatrix);

        sceneShader.unbind();
    }

    private void renderMeshes(Scene scene, boolean isShadows, Shader shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        isInstanced.setValue(0);
        isInstanced.set();

        Map<Mesh, List<GamePiece>> meshMap = scene.getMeshMap();
        for (Mesh mesh : meshMap.keySet()) {
            if (!isShadows) {
                material.setValue(mesh.getMaterial());
                material.set();
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMap().getId());
            }

            mesh.renderList(meshMap.get(mesh), (GamePiece gamePiece) -> {
                Matrix4f modelMatrix = transformation.buildModelMatrix(gamePiece);
                if (!isShadows) {
                    Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                    modelViewNonInstancedMatrix.setValue(modelViewMatrix);
                    modelViewNonInstancedMatrix.set();
                }
                Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
                modelLightViewNonInstancedMatrix.setValue(modelLightViewMatrix);
                modelLightViewNonInstancedMatrix.set();

                if (gamePiece instanceof AnimGamePiece) {
                    AnimGamePiece animGamePiece = (AnimGamePiece) gamePiece;
                    AnimatedFrame frame = animGamePiece.getCurrentFrame();
                    jointsMatrix.setValue(frame.getJointMatrices());
                    jointsMatrix.set();
                }
            });
        }
    }

    private void renderInstancedMeshes(Scene scene, boolean isShadows, Shader shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        isInstanced.setValue(1);
        isInstanced.set();

        Map<InstancedMesh, List<GamePiece>> meshMap = scene.getInstancedMeshMap();
        if (meshMap == null) return;
        for (InstancedMesh mesh : meshMap.keySet()) {
            if (!isShadows) {
                material.setValue(mesh.getMaterial());
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMap().getId());
            }
            mesh.instancedRender(meshMap.get(mesh), isShadows, transformation, viewMatrix, lightViewMatrix);
        }
    }

    private void renderParticles(Scene scene) {
        particleShader.bind();

        particleTextureSampler.set();
        particleProjectionMatrix.setValue(transformation.getProjectionMatrix());
        particleProjectionMatrix.set();

        Matrix4f viewMatrix = transformation.getViewMatrix();
        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            InstancedMesh mesh = (InstancedMesh)emitter.getBaseParticle().getMesh();

            Texture text = mesh.getMaterial().getTexture();
            particleNumCols.setValue(text.getCols());
            particleNumRows.setValue(text.getRows());

            mesh.instancedRender(emitter.getParticles(), true, transformation, viewMatrix, null);
        }

        glDepthMask(true);glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        particleShader.unbind();
    }

    private void renderSkyBox(Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            skyBoxShader.bind();
            skyBoxTextureSampler.set();

            skyBoxProjectionMatrix.setValue(transformation.getProjectionMatrix());
            skyBoxProjectionMatrix.set();

            Matrix4f viewMatrix = transformation.getViewMatrix();
            viewMatrix.m30(0);
            viewMatrix.m31(0);
            viewMatrix.m32(0);
            skyBoxModelViewMatrix.setValue(transformation.buildModelViewMatrix(skyBox, viewMatrix));
            skyBoxModelViewMatrix.set();
            skyBoxAmbientLightUniform.setValue(scene.getSkyBoxAmbientLight());
            skyBoxAmbientLightUniform.set();


            scene.getSkyBox().getMesh().render();

            skyBoxShader.unbind();
        }
    }

    private void renderLights(Matrix4f viewMatrix, Lighting lighting) {

        ambientLightUniform.setValue(lighting.getAmbientLight());
        ambientLightUniform.set();
        specularPowerUniform.set();
        // Process Point Lights
        int numLights = lighting.getPointLightList() != null ? lighting.getPointLightList().length : 0;
        PointLight[] currPointLights = new PointLight[numLights];
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(lighting.getPointLightList()[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            currPointLights[i] = currPointLight;
        }
        pointLights.setValue(currPointLights);


        // Process Spot Lights
        numLights = lighting.getSpotLightList() != null ? lighting.getSpotLightList().length : 0;
        SpotLight[] currSpotLights = new SpotLight[numLights];
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(lighting.getSpotLightList()[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            currSpotLights[i] = currSpotLight;
        }
        spotLights.setValue(currSpotLights);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(lighting.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        directionalLight.setValue(currDirLight);

        pointLights.set();
        spotLights.set();
        directionalLight.set();
    }

    private void renderHud(Window window, IHud hud) throws Exception {

        hudShader.bind();

        Matrix4f ortho = transformation.getOrtho2dProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (GamePiece gamePiece : hud.getGamePieces()) {
            Mesh mesh = gamePiece.getMesh();
            // Set ortohtaphic and model matrix for this HUD item

            projModelMatrix.setValue(transformation.buildOrtoProjModelMatrix(gamePiece, ortho));
            color.setValue(gamePiece.getMesh().getMaterial().getAmbientColour());

            projModelMatrix.set();
            color.set();
            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShader.unbind();
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public void clear() {
        Window.clear();
    }

    public void cleanup() {
        if (sceneShader != null) {
            sceneShader.cleanup();
        }
        if (depthShader != null) {
            depthShader.cleanup();
        }
        if (hudShader != null) {
            hudShader.cleanup();
        }
        if (skyBoxShader != null) {
            skyBoxShader.cleanup();
        }
    }
}
