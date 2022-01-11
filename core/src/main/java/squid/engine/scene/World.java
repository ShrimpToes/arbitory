package squid.engine.scene;

import org.joml.Vector2i;
import org.joml.Vector3f;
import squid.engine.scene.pieces.Terrain;

import java.util.ArrayList;
import java.util.List;

public class World {

    public ChunkList world;
    private Chunk baseChunk;
    private WorldGenerator generator;
    private int renderDistance;
    private static float chunkSize;

    public World(WorldGenerator generator, float chunkSize, int chunkHeight, int renderDistance) {
        world = new ChunkList();
        baseChunk = new Chunk(0, 0, chunkSize, chunkSize, chunkHeight);
        this.generator = generator;
        this.renderDistance = renderDistance;
        World.chunkSize = chunkSize;
    }

    private void genChunkPositions(int startx, int starty, int xdist, int ydist) {

        for (int x = startx; x < startx + xdist; x++) {

            for (int y = starty; y < starty + ydist; y++) {
                Chunk modelChunk = new Chunk(baseChunk);
                modelChunk.x = x * chunkSize;
                modelChunk.z = y * chunkSize;

                List<List<Chunk>> list = world.getList(x, y);
                int listx = Math.abs(x) + (x < 0 ? -1 : 0);
                int listy = Math.abs(y) + (y < 0 ? -1 : 0);

                while (list.size() <= listx) {
                    list.add(new ArrayList<>());
                }

                while (list.get(listx).size() <= listy) {
                    list.get(listx).add(baseChunk);
                }

                list.get(listx).set(listy, modelChunk);
            }
        }
    }

    public void generateStartingTerrain(WorldGenerator.WorldGenData data) {
        genChunkPositions(-renderDistance * 3, -renderDistance * 3, renderDistance * 6, renderDistance * 6);

        genTerrain(-renderDistance, -renderDistance, renderDistance * 2, renderDistance * 2, data);
    }

    public void generateVisibleTerrain(Vector3f position, WorldGenerator.WorldGenData data) {
        Vector2i chunkpos = getChunkpos(position);
        genChunkPositions( chunkpos.x - (renderDistance * 3), chunkpos.y - (renderDistance * 3), renderDistance * 6, renderDistance * 6);

        genTerrain(chunkpos.x - renderDistance, chunkpos.y - renderDistance, renderDistance * 2, renderDistance * 2, data);
    }

    public void genTerrain(int startx, int starty, int xdist, int ydist, WorldGenerator.WorldGenData data) {
        for (int x = startx; x < startx + xdist; x++) {
            for (int y = starty; y < starty + ydist; y++) {

               generateChunk(new Vector2i(x, y), data); //flipped for a reason

            }
        }
    }

    public void generateChunk(Vector2i chunkpos, WorldGenerator.WorldGenData data) {
        if (getChunk(chunkpos).terrain != null) return;
        new Thread(() -> {
            try {
                getChunk(chunkpos).terrain = generator.generateChunk(getChunk(chunkpos), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "worldgen_thread: " + chunkpos.x + ", " + chunkpos.y).start();
        System.out.println("generating chunk: " + chunkpos.x + ", " + chunkpos.y);
    }

    public Terrain[] getVisibleTerrain(Vector3f position) {
        Terrain[] visibleTerrain = new Terrain[(renderDistance * 2) * (renderDistance * 2)];
        Vector2i chunkpos = getChunkpos(position);
        int i = 0;
        for (int startx = chunkpos.x - renderDistance; startx < chunkpos.x + renderDistance; startx++) {
            for (int starty = chunkpos.y - renderDistance; starty < chunkpos.y + renderDistance; starty ++) {
                Terrain curr = world.getChunk(startx, starty).terrain;
                if (curr != null && curr.isBuilt()) { visibleTerrain[i] = curr; }
                i++;
            }
        }
        return visibleTerrain;
    }

    public Chunk getChunk(Vector3f position) {
        Vector2i v = getChunkpos(position);
        return world.getChunk(v.x, v.y);
    }

    public Chunk getChunk(Vector2i v) {
        return world.getChunk(v.x, v.y);
    }

    public static Vector2i getChunkpos(Vector3f position) {
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

        // no?

//        public void generateTerrain(WorldGenerator generator, WorldGenerator.WorldGenData genData) {
//            try {
//                terrain = generator.generateChunk(this, genData);
//            } catch (Exception e) {
//                e.printStackTrace();
//                this.terrain = null;
//            }
//        }

        public boolean contains(Vector3f position) {
            return (position.x >= x && position.x <= x + xWidth) && (position.z >= z && position.z <= z + zWidth);
        }
    }

    public static class ChunkList {
        public List<List<Chunk>> posXZ;     //     |posX
        public List<List<Chunk>> posXnegZ;  //_____|____
        public List<List<Chunk>> negXZ;     //     |negX
        public List<List<Chunk>> negXposZ;  // negZ|posZ

        public ChunkList() {
            posXZ = new ArrayList<>();
            posXnegZ = new ArrayList<>();
            negXZ = new ArrayList<>();
            negXposZ = new ArrayList<>();
        }

        public List<List<Chunk>> getList(int x, int z) {
            if (x >= 0) {
                if (z >= 0) {
                    return posXZ;
                } else {
                    return posXnegZ;
                }
            } else {
                if (z >= 0) {
                    return negXposZ;
                } else {
                    return negXZ;
                }
            }
        }

        public Chunk getChunk(int x, int z) {
            if (x == 0) {
                if (z == 0) {
                    return posXZ.get(0).get(0);
                }
                return getList(0, z).get(0).get(getZ(z));
            } else {
                return getList(x, z).get(getX(x)).get(getZ(z));
            }
        }

        private int getX(int x) {
            if (x < 0) {
                return Math.abs(x) - 1;
            } else {
                return x;
            }
        }

        private int getZ(int z) {
            if (z < 0) {
                return Math.abs(z) - 1;
            } else {
                return z;
            }
        }
    }
}
