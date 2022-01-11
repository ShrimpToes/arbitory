package squid.engine.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import squid.engine.scene.pieces.GamePiece;

public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f modelLightMatrix;
    private final Matrix4f modelLightViewMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f lightViewMatrix;
    private final Matrix4f orthoProjMatrix;
    private final Matrix4f ortho2dMatrix;
    private final Matrix4f orthoModelMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        modelLightMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
        ortho2dMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        orthoProjMatrix.identity();
        orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
        return orthoProjMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        return updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), viewMatrix);
    }

    public void setLightViewMatrix(Matrix4f lightViewMatrix) {
        this.lightViewMatrix.set(lightViewMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public final Matrix4f getOrtho2dProjectionMatrix(float left, float right, float bottom, float top) {
        ortho2dMatrix.identity();
        ortho2dMatrix.setOrtho2D(left, right, bottom, top);
        return ortho2dMatrix;
    }

    public Matrix4f buildModelMatrix(GamePiece gamePiece) {
        Quaternionf rotation = gamePiece.getRotation();
        modelMatrix.identity().translate(gamePiece.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gamePiece.getScale());
        return modelMatrix;
    }

    public Matrix4f buildModelViewMatrix(GamePiece gamePiece, Matrix4f viewMatrix) {
        Quaternionf rotation = gamePiece.getRotation();
        modelMatrix.identity().translate(gamePiece.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gamePiece.getScale());
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(GamePiece gamePiece, Matrix4f matrix) {
        Quaternionf rotation = gamePiece.getRotation();
        modelLightMatrix.identity().translate(gamePiece.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gamePiece.getScale());
        modelLightViewMatrix.set(matrix);
        return modelLightViewMatrix.mul(modelLightMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        modelLightViewMatrix.set(viewMatrix);
        return modelLightViewMatrix.mul(modelMatrix);
    }

    public Matrix4f buildOrthoProjModelMatrix(GamePiece gamePiece, Matrix4f orthoMatrix) {
        Quaternionf rotation = gamePiece.getRotation();
        modelMatrix.identity().translate(gamePiece.getPosition()).
                rotateX((float) Math.toRadians(-rotation.x)).
                rotateY((float) Math.toRadians(-rotation.y)).
                rotateZ((float) Math.toRadians(-rotation.z)).
                scale(gamePiece.getScale());
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }
}
