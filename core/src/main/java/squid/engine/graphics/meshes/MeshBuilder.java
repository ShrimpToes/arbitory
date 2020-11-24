package squid.engine.graphics.meshes;

import org.joml.Vector4f;
import squid.engine.graphics.textures.Material;
import squid.engine.graphics.textures.Texture;
import squid.engine.scene.pieces.GamePiece;

import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {

    private static List<IBuffer> buffers = new ArrayList<>();

    public static void addMesh(IBuffer buffer) {
        buffers.add(buffer);
    }

    public static void buildMeshes(int amount) {
        List<IBuffer> donebuffers = new ArrayList<>();
        for (int i = 0; i < Math.min(amount, buffers.size()); i++) {
            buffers.get(i).build();
            donebuffers.add(buffers.get(i));
        }
        if (donebuffers.size() > 0) {
            for (IBuffer buffer : donebuffers) {
                buffers.remove(buffer);
            }
        }
    }

    public static interface IBuffer {
        void build();
    }

    public static class MeshBuffer implements IBuffer {
        public float[] verticesarr;
        public int[] indicesarr;
        public float[] textCoordsarr;
        public float[] normalsarr;
        public int instances;
        public GamePiece gamePiece;
        public MaterialBuffer materialBuffer;

        public MeshBuffer(float[] vertices, int[] indices, float[] textCoords, float[] normals, GamePiece gamePiece, MaterialBuffer materialBuffer) {
            verticesarr = vertices;
            indicesarr = indices;
            textCoordsarr = textCoords;
            normalsarr = normals;
            this.gamePiece = gamePiece;
            this.materialBuffer = materialBuffer;
        }

        public MeshBuffer(float[] vertices, int[] indices, float[] textCoords, float[] normals) {
            verticesarr = vertices;
            indicesarr = indices;
            textCoordsarr = textCoords;
            normalsarr = normals;
        }

        public MeshBuffer() {
        }

        public Mesh buildMesh() {
            Mesh mesh;
            if (instances > 1) {
                mesh = new InstancedMesh(verticesarr, indicesarr, textCoordsarr, normalsarr, instances);
            } else {
                mesh = new Mesh(verticesarr, indicesarr, textCoordsarr, normalsarr);
            }
            return mesh;
        }

        public void build() {
            Mesh mesh = buildMesh();
            if (materialBuffer != null) {
                materialBuffer.build();
                mesh.setMaterial(materialBuffer.material);
            }

            gamePiece.setMesh(mesh);
        }
    }

    public static class HeightMapMeshBuffer extends MeshBuffer {

        private final float[][] heights;
        private final float minY, maxY;
        public final int verticesPerCol, verticesPerRow;

        public HeightMapMeshBuffer(float[] vertices, int[] indices, float[] textCoords, float[] normals,
                                   float[][] heights, float minY, float maxY,
                                   int verticesPerCol, int verticesPerRow, GamePiece gamePiece, MaterialBuffer materialBuffer) {
            super(vertices, indices, textCoords, normals, gamePiece, materialBuffer);
            this.heights = heights;
            this.minY = minY;
            this.maxY = maxY;
            this.verticesPerCol = verticesPerCol;
            this.verticesPerRow = verticesPerRow;
        }

        @Override
        public void build() {
            HeightMap heightmap = new HeightMap(verticesarr, indicesarr, textCoordsarr, normalsarr,
                    heights, minY, maxY, verticesPerCol, verticesPerRow, materialBuffer.material);
            if (materialBuffer != null) {
                materialBuffer.build();
                heightmap.setMaterial(materialBuffer.material);
            }

            gamePiece.setMesh(heightmap);
        }
    }

    public static class MaterialBuffer implements IBuffer {
        public Material material;
        private String texture;
        private String normalMap;

        public MaterialBuffer(Material material) {
            this.material = material;
        }

        public MaterialBuffer(Material material, String texture) {
            this(material);
            this.texture = texture;
        }

        public MaterialBuffer(Material material, String texture, String normalMap) {
            this(material, texture);
            this.normalMap = normalMap;
        }

        public void build() {
            try {
                if (texture != null) {
                    material.setTexture(new Texture(texture));
                }
                if (normalMap != null) {
                    material.setNormalMap(new Texture(normalMap));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}
