package squid.engine.scene;

import squid.engine.graphics.textures.Material;
import squid.engine.graphics.Mesh;
import squid.engine.graphics.textures.Texture;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.utils.readers.obj.OBJReader;

public class SkyBox extends GamePiece {
    public SkyBox(String objModel, String textureFile, float scale) throws Exception {
        super();
        Mesh skyBoxMesh = OBJReader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
        this.setScale(scale);
    }
}
