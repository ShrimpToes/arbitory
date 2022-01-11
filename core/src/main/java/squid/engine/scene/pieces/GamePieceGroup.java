package squid.engine.scene.pieces;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import squid.engine.graphics.meshes.Mesh;

public class GamePieceGroup extends GamePiece {

    private GamePiece[] gamePieces;

    public GamePieceGroup(GamePiece[] gamePieces) {
        this.gamePieces = gamePieces;
        for (GamePiece gamePiece : gamePieces) {
            gamePiece.parent = this;
        }
    }

    public GamePiece[] getGamePieces() {
        return gamePieces;
    }

    public void setGamePieces(GamePiece[] gamePieces) {
        this.gamePieces = gamePieces;
    }
}
