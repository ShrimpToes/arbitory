package squid.game;

import org.joml.Vector2i;
import org.joml.Vector3f;
import squid.engine.scene.pieces.Terrain;

import java.util.HashMap;
import java.util.Map;

public class World {

    public Map<int[], Chunk> world;
    private Chunk baseChunk;
    private WorldGenerator generator;
    private int renderDistance, renderWidth;
    private float chunkSize;

    public World(WorldGenerator generator, float chunkSize, int chunkHeight, int renderDistance) {
        world = new HashMap<>();
        baseChunk = new Chunk(0, 0, chunkSize, chunkSize, chunkHeight);
        this.generator = generator;
        this.renderDistance = renderDistance;
        renderWidth = (renderDistance * 2) + 1;
        this.chunkSize = chunkSize;
    }

    private void genChunkPositions(int startx, int starty, int xdist, int ydist) {
        for (int x = startx; x < xdist; x++) {
            for (int y = starty; y < ydist; y++) {
                Chunk modelChunk = baseChunk;
                modelChunk.x = x * chunkSize;
                modelChunk.z = y * chunkSize;
                world.put(new int[]{x, y}, modelChunk);
            }
        }
    }

    public int getRenderWidth() {
        return renderWidth;
    }

    public void generateStartingTerrain(WorldGenerator.WorldGenData data) {
        genChunkPositions(-renderDistance * 3, -renderDistance * 3, renderDistance * 6, renderDistance * 6);

        genTerrain(-renderDistance, -renderDistance, renderDistance * 2, renderDistance  * 2, data);
    }

    public void genTerrain(int startx, int starty, int xdist, int ydist, WorldGenerator.WorldGenData data) {
        for (int x = startx; x < xdist; x++) {
            for (int y = starty; y < ydist; y++) {
                generateChunk(new Vector2i(x, y), data);
            }
        }
    }

    public void generateChunk(Vector2i chunkpos, WorldGenerator.WorldGenData data) {
        new Thread(() -> {
            try {
                getChunk(chunkpos).terrain = generator.generateChunk(getChunk(chunkpos), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Terrain[] getVisibleTerrain(Vector3f position) {
        Terrain[] visibleTerrain = new Terrain[renderWidth * renderWidth];
        Vector2i chunkpos = getChunkpos(position);
        int i = 0;
        for (int startx = chunkpos.x - renderDistance; startx < chunkpos.x + renderDistance; startx++) {
            for (int starty = chunkpos.y - renderDistance; starty < chunkpos.y + renderDistance; starty ++) {
                visibleTerrain[i] = world.get(new int[]{startx, starty}).terrain;
                i++;
            }
        }
        return visibleTerrain;
    }

    public Chunk getChunk(Vector3f position) {
        Vector2i v = getChunkpos(position);
        return world.get(new int[]{v.x, v.y});
    }

    public Chunk getChunk(Vector2i v) {
        return world.get(new int[]{v.x, v.y});
    }

    public Vector2i getChunkpos(Vector3f position) {
        int x = (int) (position.x / chunkSize);
        int y = (int) (position.z / chunkSize);
        return new Vector2i(x, y);
    }

    public static class Chunk {
        public float x, z, xWidth, zWidth, height;

        public Terrain terrain;

        public Chunk(float x, float z, float xWidth, float zWidth, float height) {
            this.x = x;
            this.z = z;
            this.xWidth = xWidth;
            this.zWidth = zWidth;
            this.height = height;
        }

        public Chunk(Chunk chunk) {
            this.x = chunk.x;
            this.z = chunk.z;
            this.xWidth = chunk.xWidth;
            this.zWidth = chunk.zWidth;
            this.height = chunk.height;
        }

        public void generateTerrain(WorldGenerator generator, WorldGenerator.WorldGenData genData) {
            try {
                terrain = generator.generateChunk(this, genData);
            } catch (Exception e) {
                e.printStackTrace();
                this.terrain = null;
            }
        }

        public boolean contains(Vector3f position) {
            return (position.x >= x && position.x <= x + xWidth) && (position.z >= z && position.z <= z + zWidth);
        }
    }
}
