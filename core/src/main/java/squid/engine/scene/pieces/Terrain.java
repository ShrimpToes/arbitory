package squid.engine.scene.pieces;

import org.joml.Vector3f;
import squid.engine.graphics.meshes.HeightMap;
import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.graphics.textures.Material;
import squid.engine.graphics.textures.Texture;
import squid.engine.utils.HeightMapReader;

public class Terrain {

    private final GamePiece gamePiece;
    private final Box2D boundingBox;
    private final HeightMap heightMap;
    private final int verticesPerCol;
    private final int verticesPerRow;

    public Terrain(float scale, float minY, float maxY, HeightMap heightMap) throws Exception {

        //make maxY greater than minY

        this.heightMap = heightMap;
        verticesPerCol = heightMap.verticesPerColumn;
        verticesPerRow = heightMap.verticesPerRow;

        gamePiece = new GamePiece(heightMap);
        gamePiece.setScale(scale);

        boundingBox = new Box2D(gamePiece.getPosition().x, gamePiece.getPosition().y, gamePiece.getScale(), maxY - minY);
    }

    public Terrain(float scale, float minY, float maxY, MeshBuilder.HeightMapMeshBuffer meshBuffer) {
        this.heightMap = null;

        verticesPerCol = meshBuffer.verticesPerCol;
        verticesPerRow = meshBuffer.verticesPerRow;

        gamePiece = meshBuffer.gamePiece;

        boundingBox = new Box2D(gamePiece.getPosition().x, gamePiece.getPosition().y, gamePiece.getScale(), maxY - minY);

        MeshBuilder.addMesh(meshBuffer);
    }

    public Terrain(float scale, float minY, float maxY, String heightMapFile, String textureFile, int textInc) throws Exception {
        this(scale, minY, maxY, HeightMapReader.buildMesh(minY, maxY, heightMapFile, textureFile, textInc));
    }

    public Terrain(float scale, float minY, float maxY, float[][] heights, int width, int height, String textureFile, int textInc) throws Exception {
        this(scale, minY, maxY, HeightMapReader.buildMap(heights, width, height, textInc, minY, maxY, new Material(new Texture(textureFile))));
    }

    public float getHeight(Vector3f position) {
        float result;

        Vector3f[] triangle = getTriangle(position, boundingBox, gamePiece);
        result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);

        return result;
    }

    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, GamePiece terrainBlock) {

        if (heightMap == null) { return new Vector3f[]{
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 0, 1)}; }

        // Get the column and row of the heightmap associated to the current position
        float cellWidth = boundingBox.width / (float) verticesPerCol;
        float cellHeight = boundingBox.height / (float) verticesPerRow;
        int col = (int) ((position.x - boundingBox.x) / cellWidth);
        int row = (int) ((position.z - boundingBox.y) / cellHeight);

        Vector3f[] triangle = new Vector3f[3];
        triangle[1] = new Vector3f(
                boundingBox.x + col * cellWidth,
                getWorldHeight(row + 1, col, terrainBlock),
                boundingBox.y + (row + 1) * cellHeight);
        triangle[2] = new Vector3f(
                boundingBox.x + (col + 1) * cellWidth,
                getWorldHeight(row, col + 1, terrainBlock),
                boundingBox.y + row * cellHeight);
        if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x)) {
            triangle[0] = new Vector3f(
                    boundingBox.x + col * cellWidth,
                    getWorldHeight(row, col, terrainBlock),
                    boundingBox.y + row * cellHeight);
        } else {
            triangle[0] = new Vector3f(
                    boundingBox.x + (col + 1) * cellWidth,
                    getWorldHeight(row + 2, col + 1, terrainBlock),
                    boundingBox.y + (row + 1) * cellHeight);
        }

        return triangle;
    }

    protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x) {
        float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
        return z;
    }

    protected float getWorldHeight(int row, int col, GamePiece gamePiece) {
        float y = this.heightMap.getHeight(row, col);
        return y * gamePiece.getScale() + gamePiece.getPosition().y;
    }

    protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {
        // Plane equation ax+by+cz+d=0
        float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
        float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
        float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
        float d = -(a * pA.x + b * pA.y + c * pA.z);
        // y = (-d -ax -cz) / b
        float y = (-d - a * x - c * z) / b;
        return y;
    }

    private Box2D getBoundingBox(GamePiece terrainBlock) {
        float scale = terrainBlock.getScale();
        Vector3f position = terrainBlock.getPosition();

        float topLeftX = HeightMapReader.STARTX * scale + position.x;
        float topLeftZ = HeightMapReader.STARTZ * scale + position.z;
        float width = Math.abs(HeightMapReader.STARTX * 2) * scale;
        float height = Math.abs(HeightMapReader.STARTZ * 2) * scale;
        Box2D boundingBox = new Box2D(topLeftX, topLeftZ, width, height);
        return boundingBox;
    }

    public boolean isBuilt() {
        return gamePiece.hasMesh();
    }

    public GamePiece getGamePiece() {
        return gamePiece;
    }

    public static class Box2D {

        public float x;

        public float y;

        public float width;

        public float height;

        public Box2D(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float x2, float y2) {
            return x2 >= x
                    && y2 >= y
                    && x2 < x + width
                    && y2 < y + height;
        }
    }
}
