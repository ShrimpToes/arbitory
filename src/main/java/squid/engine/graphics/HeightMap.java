package squid.engine.graphics;

import squid.engine.graphics.textures.Material;

public class HeightMap extends Mesh {

    private final float[][] heights;
    private final float minY, maxY;

    public final int verticesPerColumn, verticesPerRow;

    public HeightMap(float[] vertices, int[] indices, float[] textCoords, float[] normals,
                     float[][] heights, float minY, float maxY,
                     int verticesPerCol, int verticesPerRow, Material material) {
        super(vertices, indices, textCoords, normals);
        super.setMaterial(material);
        this.heights = heights;
        this.minY = minY;
        this.maxY = maxY;
        this.verticesPerColumn = verticesPerCol;
        this.verticesPerRow = verticesPerRow;
    }

    public float getHeight(int row, int col) {
        float result = 0;
        if ( row >= 0 && row < heights.length ) {
            if ( col >= 0 && col < heights[row].length ) {
                result = heights[row][col];
            }
        }
        return result;
    }
}
