package squid.engine.utils.readers.obj;

import org.joml.Vector2f;
import org.joml.Vector3f;
import squid.engine.graphics.meshes.InstancedMesh;
import squid.engine.graphics.meshes.Mesh;
import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OBJReader {

    public static MeshBuilder.MeshBuffer loadMeshBuffer(String filename) throws Exception {
        return loadMeshBuffer(filename, 1);
    }

    public static Mesh loadMesh(String filename) throws Exception {
        return loadMesh(filename, 1);
    }

    public static Mesh loadMesh(String filename, int instances) throws Exception {
        return loadMeshBuffer(filename, instances).buildMesh();
    }

    public static MeshBuilder.MeshBuffer loadMeshBuffer(String fileName, int instances) throws Exception {
        MeshBuilder.MeshBuffer buffer = new MeshBuilder.MeshBuffer();

        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;
                default:
                    break;
            }
        }

        return reorderLists(vertices, textures, normals, faces, instances, buffer);
    }

    private static MeshBuilder.MeshBuffer reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList,
                                                       List<Vector3f> normList, List<Face> facesList, int instances,
                                                       MeshBuilder.MeshBuffer buffer) {

        List<Integer> indices = new ArrayList<>();
        // Create position array
        float[] posArr = new float[facesList.size() * 9];


        //  This implementation does not fulfill the specification of wavefront obj entirely in terms of texture
        //coordinates in the case when there are more texture coordinates than vertices when the file uses indexed
        //vertices rather than repeated vertices.
        /*
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        */


        float[] textCoordArray = new float[facesList.size() * 6];
        float[] normArray = new float[facesList.size() * 9];

        int posIndex = 0;
        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, posList, textCoordArray, normArray, posArr, posIndex);
                posIndex++;
            }
        }
        int[] indicesArray = new int[indices.size()];


        indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        buffer.verticesarr = posArr;
        buffer.indicesarr = indicesArray;
        buffer.textCoordsarr = textCoordArray;
        buffer.normalsarr = normArray;
        buffer.instances = instances;

        return buffer;
    }

    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Integer> indicesList, List<Vector3f> vertices,
                                          float[] texCoordArr, float[] normArr, float[] posArr, int posIndex) {

        // Set index for vertex coordinates

        //indicesList.add(posIndex);
        indicesList.add(posIndex);

        if (indices.idxPos >= 0) {
            Vector3f position = vertices.get(indices.idxPos);
            posArr[posIndex * 3] = position.x;
            posArr[posIndex * 3 + 1] = position.y;
            posArr[posIndex * 3 + 2] = position.z;
        }

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }

        // Theoretically preserves ordering of vertices except with copied vertices for each index
        // This copies vertices as necessary to align texture coordinates. There's probably a better way about this
        // Yes, it does circumvent the indexed drawing



        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }


    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }

    protected static class Face {
        private IdxGroup[] idxGroups = new IdxGroup[3];

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }
}
