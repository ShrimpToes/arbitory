package squid.engine.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.utils.Transformation;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

public class InstancedMesh extends Mesh {
    private static final int VECTOR4F_SIZE_BYTES = 4 * 4;
    private static final int MATRIX_SIZE_BYTES = 4 * VECTOR4F_SIZE_BYTES;
    private static final int MATRIX_SIZE_FLOATS = 4 * 4;

    private final int numInstances;
    private final int modelViewVBO, modelLightViewVBO;
    private FloatBuffer modelViewBuffer, modelLightViewBuffer;

    public InstancedMesh(float[] vertices, int[] indices, float[] textCoords, float[] normals, int numInstances) {
        super(vertices, indices, textCoords, normals);
        this.numInstances = numInstances;
        glBindVertexArray(vaoId);

        modelViewVBO = glGenBuffers();
        vboList.add(modelViewVBO);
        modelViewBuffer = MemoryUtil.memAllocFloat(numInstances * MATRIX_SIZE_FLOATS);
        glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
        int start = 5;
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
            glVertexAttribDivisor(start, 1);
            glEnableVertexAttribArray(start);
            start++;
        }

        modelLightViewVBO = glGenBuffers();
        vboList.add(modelLightViewVBO);
        modelLightViewBuffer = MemoryUtil.memAllocFloat(numInstances * MATRIX_SIZE_FLOATS);
        glBindBuffer(GL_ARRAY_BUFFER, modelLightViewVBO);
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
            glVertexAttribDivisor(start, 1);
            glEnableVertexAttribArray(start);
            start++;
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (this.modelViewBuffer != null) {
            MemoryUtil.memFree(this.modelViewBuffer);
            this.modelViewBuffer = null;
        }
        if (this.modelLightViewBuffer != null) {
            MemoryUtil.memFree(this.modelLightViewBuffer);
            this.modelLightViewBuffer = null;
        }
    }


    @Override
    protected void initRender() {
        super.initRender();
        int start = 5;
        int numElements = 4 * 2;
        for (int i = 0; i < numElements; i++) {
            glEnableVertexAttribArray(start + i);
        }
    }

    @Override
    protected void finishRender() {
        int start = 5;
        int numElements = 4 * 2;
        for (int i = 0; i < numElements; i++) {
            glDisableVertexAttribArray(start + i);
        }
        super.finishRender();
    }

    public void instancedRender(List<GamePiece> gamePieces, boolean useDepthMap, Transformation transformation,
                                 Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        initRender();
        int length = gamePieces.size();
        int chunkSize = numInstances;
        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            List<GamePiece> subList = gamePieces.subList(i, end);
            renderChunk(subList, useDepthMap, transformation, viewMatrix, lightViewMatrix);
        }
    }

    private void renderChunk(List<GamePiece> gamePieces, boolean depthMap, Transformation transformation,
                             Matrix4f viewMatrix, Matrix4f ligthViewMatrix) {
        modelViewBuffer.clear();
        modelLightViewBuffer.clear();

        int i = 0;

        for (GamePiece gamePiece : gamePieces) {
            Matrix4f modelMatrix = transformation.buildModelMatrix(gamePiece);
            if (!depthMap) {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                modelViewMatrix.get(MATRIX_SIZE_FLOATS * i, modelViewBuffer);
            }
            Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, ligthViewMatrix);
            modelLightViewMatrix.get(MATRIX_SIZE_FLOATS * i, modelLightViewBuffer);
            i++;
        }
        glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
        glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, modelLightViewVBO);
        glBufferData(GL_ARRAY_BUFFER, modelLightViewBuffer, GL_DYNAMIC_DRAW);
        glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, gamePieces.size());
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }
}
