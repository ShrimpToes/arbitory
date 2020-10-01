package squid.engine.graphics;

import org.lwjgl.system.MemoryUtil;
import squid.engine.graphics.textures.Material;
import squid.engine.graphics.textures.Texture;
import squid.engine.scene.pieces.GamePiece;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {

    public static final int MAX_WEIGHTS = 4;
    protected final int vaoId;
    protected final List<Integer> vboList;
    protected final int verticesCount;
    protected Material material;

    public Mesh(float[] vertices, int[] indices, float[] textCoords, float[] normals) {
        this(vertices, indices, textCoords, normals,
                Mesh.createEmptyIntArray(Mesh.MAX_WEIGHTS * vertices.length / 3, 0),
                Mesh.createEmptyFloatArray(Mesh.MAX_WEIGHTS * vertices.length / 3, 0));
    }

    public Mesh(float[] vertices, int[] indices, float[] textCoords, float[] normals, int[] jointIndices, float[] weights) {
        FloatBuffer verticesBuffer = null;
        IntBuffer idxBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        FloatBuffer weightsBuffer = null;
        IntBuffer jointIndicesBuffer = null;
        vboList = new ArrayList<>();
        try {


            verticesCount = indices.length;

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //vertices
            int vboId = glGenBuffers();
            vboList.add(vboId);
            verticesBuffer = memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //indices
            vboId = glGenBuffers();
            vboList.add(vboId);
            idxBuffer = MemoryUtil.memAllocInt(indices.length);
            idxBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuffer, GL_STATIC_DRAW);

            //texture coordinates
            vboId = glGenBuffers();
            vboList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //normals (whatever tf they are)
            vboId = glGenBuffers();
            vboList.add(vboId);
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            if (normalsBuffer.capacity() > 0) {
                normalsBuffer.put(normals).flip();
            } else {
                // Create empty structure
                normalsBuffer = MemoryUtil.memAllocFloat(vertices.length);
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            //weights
            vboId = glGenBuffers();
            vboList.add(vboId);
            weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
            weightsBuffer.put(weights).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

            //jointIndices
            vboId = glGenBuffers();
            vboList.add(vboId);
            jointIndicesBuffer = MemoryUtil.memAllocInt(jointIndices.length);
            jointIndicesBuffer.put(jointIndices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);


            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null) {
                memFree(verticesBuffer);
            }
            if (idxBuffer != null) {
                memFree(idxBuffer);
            }
            if (textCoordsBuffer != null) {
                memFree(textCoordsBuffer);
            }
            if (jointIndicesBuffer != null) {
                memFree(jointIndicesBuffer);
            }
            if (weightsBuffer != null) {
                memFree(weightsBuffer);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return verticesCount;
    }

    protected void initRender() {
        Texture texture = material.getTexture();
        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        Texture normalMap = material.getNormalMap();
        if ( normalMap != null ) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, normalMap.getId());
        }
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
    }

    public void render() {
        initRender();
        drawTriangles();
        finishRender();
    }

    public void drawTriangles() {
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
    }

    protected void finishRender() {
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void renderList(List<GamePiece> gamePieces, Consumer<GamePiece> consumer) {
        initRender();
        for (GamePiece gamePiece : gamePieces) {
            consumer.accept(gamePiece);
            drawTriangles();
        }
        finishRender();
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for (Integer id : vboList) {
            glDeleteBuffers(id);
        }

        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }


    private static float[] createEmptyFloatArray(int length, float defaultValue) {
        float[] result = new float[length];
        Arrays.fill(result, defaultValue);
        return result;
    }

    private static int[] createEmptyIntArray(int length, int defaultValue) {
        int[] result = new int[length];
        Arrays.fill(result, defaultValue);
        return result;
    }
}
