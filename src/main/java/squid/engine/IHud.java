package squid.engine;

import squid.engine.scene.pieces.GamePiece;

public interface IHud {
    GamePiece[] getGamePieces() throws Exception;

    default void cleanup() throws Exception {
        GamePiece[] gamePieces = getGamePieces();
        for (GamePiece gameItem : gamePieces) {
            gameItem.getMesh().cleanup();
        }
    }
}
