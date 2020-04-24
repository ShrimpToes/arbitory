package squid.game;

import squid.engine.scene.pieces.FastNoise;
import squid.engine.scene.pieces.Terrain;

public class WorldGenerator {
    private FastNoise noise;

    public WorldGenerator(int seed, float frequency) {
        noise = new FastNoise(seed);
        noise.SetFrequency(frequency);
    }

    public Terrain generateChunk(Chunk chunk, String textureFile, int textInc, int stepsX, int stepsY) throws Exception {

        float currHeight;
        float [][] heights = new float[stepsX][stepsY];
        int currStepX = 0;

        float stepXSize = chunk.xWidth / stepsX;
        float stepYSize = chunk.yWidth / stepsY;

        for (float x = chunk.x; x <= chunk.xWidth + chunk.x - (stepXSize / 2); x+= stepXSize) {
            int currStepY = 0;
            for (float y = chunk.z; y <= chunk.yWidth + chunk.z - (stepYSize / 2); y+= stepYSize) {
                currHeight = noise.GetCubicFractal(x, y);
                heights[currStepX][currStepY] = currHeight;
                currStepY++;
            }
            currStepX++;
        }

        Terrain terrainchunk = new Terrain(chunk.xWidth, 0, chunk.height, heights, stepsX, stepsY, textureFile, textInc);
        terrainchunk.getGamePiece().setPosition(chunk.z, 0, chunk.x);
        return terrainchunk;
    }


    public static class Chunk {
        public float x, z, xWidth, yWidth, height;

        public Chunk(float x, float z, float xWidth, float yWidth, float height) {
            this.x = x;
            this.z = z;
            this.xWidth = xWidth;
            this.yWidth = yWidth;
            this.height = height;
        }
    }
}
