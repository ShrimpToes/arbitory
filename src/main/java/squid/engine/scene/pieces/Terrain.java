package squid.engine.scene.pieces;

import org.joml.Vector3f;
import squid.engine.utils.HeightMapReader;

public class Terrain {

    private final GamePiece[] gamePieces;
    private final HeightMap heightMap;
    private final int blocksPerRow;
    private final Box2D[][] boundingBoxes;
    private final int verticesPerCol;
    private final int verticesPerRow;

    @SuppressWarnings("unchecked")
    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMapFile, String textureFile, int textInc) throws Exception {

        this.blocksPerRow = blocksPerRow;
        boundingBoxes = new Box2D[blocksPerRow][blocksPerRow];

        gamePieces = new GamePiece[blocksPerRow * blocksPerRow];

        heightMap = HeightMapReader.buildMesh(minY, maxY, heightMapFile, textureFile, textInc);
        verticesPerCol = heightMap.verticesPerColumn;
        verticesPerRow = heightMap.verticesPerRow;

        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapReader.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapReader.getZLength();

                GamePiece terrainBlock = new GamePiece(heightMap);
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gamePieces[row * blocksPerRow + col] = terrainBlock;
                boundingBoxes[row][col] = getBoundingBox(terrainBlock);
            }
        }
    }

    public float getHeight(Vector3f position) {
        float result = Float.MIN_VALUE;
        // For each terrain block we get the bounding box, translate it to view coodinates
        // and check if the position is contained in that bounding box
        Box2D boundingBox = null;
        boolean found = false;
        GamePiece terrainBlock = null;
        for (int row = 0; row < blocksPerRow && !found; row++) {
            for (int col = 0; col < blocksPerRow && !found; col++) {
                terrainBlock = gamePieces[row * blocksPerRow + col];
                boundingBox = boundingBoxes[row][col];
                found = boundingBox.contains(position.x, position.z);
            }
        }

        // If we have found a terrain block that contains the position we need
        // to calculate the height of the terrain on that position
        if (found) {
            Vector3f[] triangle = getTriangle(position, boundingBox, terrainBlock);
            result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
        }

        return result;
    }

    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, GamePiece terrainBlock) {
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

    public GamePiece[] getGamePieces() {
        return gamePieces;
    }

    static class Box2D {

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
