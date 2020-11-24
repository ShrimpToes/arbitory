package squid.engine.scene;

import org.joml.Vector3f;
import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.graphics.textures.Material;
import squid.engine.scene.pieces.FastNoise;
import squid.engine.graphics.meshes.HeightMap;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.scene.pieces.Terrain;
import squid.engine.utils.HeightMapReader;
import squid.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WorldGenerator {
    private FastNoise noise;

    public WorldGenerator(int seed, float frequency) {
        noise = new FastNoise(seed);
        noise.SetFrequency(frequency);
    }

    public Terrain generateChunk(World.Chunk chunk, WorldGenData gendata) throws Exception {
        int stepsX = gendata.stepsX;
        int stepsY = gendata.stepsY;

        noise.SetNoiseType(gendata.noiseType);
        Vector3f[][] positions = new Vector3f[stepsX][stepsY];


        float stepXSize = chunk.xWidth / (stepsX - 1);
        float stepYSize = chunk.zWidth / (stepsY - 1);

        for (int currStepX = 0; currStepX < stepsX; currStepX++) {
            float xpos = currStepX * stepXSize;
            float currX = chunk.x + xpos;
            for (int currStepY = 0; currStepY < stepsY; currStepY++) {
                float zpos = currStepY * stepYSize;
                float currZ = chunk.z + zpos;

                float currHeight = noise.GetNoise(currX, currZ);

                currHeight++;
                currHeight = currHeight / 2;
                currHeight = currHeight * chunk.height;

                positions[currStepX][currStepY] = new Vector3f(zpos, currHeight, xpos);
            }
        }

        Material material = new Material();
        MeshBuilder.MaterialBuffer materialBuffer = new MeshBuilder.MaterialBuffer(material, gendata.textureFile);

        GamePiece terrainPiece = new GamePiece();
        terrainPiece.setPosition(chunk.x, 0, chunk.z);
        terrainPiece.setScale(1);

        return new Terrain(chunk.xWidth, 0, chunk.height,
                buildMap(positions, gendata.textInc, stepsX, stepsY, 0, chunk.height, materialBuffer, terrainPiece));
    }

    private MeshBuilder.HeightMapMeshBuffer buildMap(Vector3f[][] positions, int textInc, int stepsX, int stepsY, float minY, float maxY, MeshBuilder.MaterialBuffer materialBuffer, GamePiece gamePiece) {
        List<Float> textCoords = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[][] heights = new float[stepsX][stepsY];

        for (int row = 0; row  < stepsX; row++) {
            for (int col = 0; col < stepsY; col++) {
                Vector3f currPos = positions[row][col];

                textCoords.add((float) textInc * (float) col / (float) stepsX);
                textCoords.add((float) textInc * (float) row / (float) stepsY);

                heights[row][col] = currPos.y;

                vertices.add(currPos.z);
                vertices.add(currPos.y);
                vertices.add(currPos.x);

                if (col < stepsX - 1 && row < stepsY - 1) {
                    int leftTop = row * stepsX + col;
                    int leftBottom = (row + 1) * stepsX + col;
                    int rightBottom = (row + 1) * stepsX + col + 1;
                    int rightTop = row * stepsX + col + 1;

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);
                }
            }
        }

        float[] verticesArr = Utils.listToArray(vertices);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] textCoordsArr = Utils.listToArray(textCoords);
        float[] normalsArr = HeightMapReader.calcNormals(verticesArr, stepsX, stepsY);

        return new MeshBuilder.HeightMapMeshBuffer(verticesArr, indicesArr, textCoordsArr, normalsArr, heights, minY, maxY, stepsY, stepsX, gamePiece, materialBuffer);
    }

    public static class WorldGenData {

        public WorldGenData() {

        };

        public WorldGenData(String textureFile, int stepsX, int stepsY, int textInc, FastNoise.NoiseType noiseType) {
            this.textureFile = textureFile;
            this.stepsX = stepsX;
            this.stepsY = stepsY;
            this.textInc = textInc;
            this.noiseType = noiseType;
        }

        public String textureFile;
        public int stepsX;
        public int stepsY;
        public int textInc;
        public FastNoise.NoiseType noiseType;
    }
}
