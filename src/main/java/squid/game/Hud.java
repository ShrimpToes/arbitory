package squid.game;

import org.joml.Vector4f;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.IHud;
import squid.engine.scene.pieces.TextPiece;
import squid.engine.graphics.textures.FontTexture;

import java.awt.*;

public class Hud implements IHud {

    private static final String CHARSET = "ISO-8859-1";
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 30);

    private TextPiece textPiece;
    private TextPiece monsterFrame;
    private GamePiece[] gamePieces;

    public Hud(String text) throws Exception {
        this.textPiece = new TextPiece(text, new FontTexture(FONT, CHARSET));
        this.textPiece.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        monsterFrame = new TextPiece(text, new FontTexture(FONT, CHARSET));
        this.textPiece.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        monsterFrame.setPosition(-1f, 0f, 1f);

        gamePieces = new GamePiece[]{textPiece, monsterFrame};
    }

    public void setStatusText(String statusText) {
        this.textPiece.setText(statusText);
    }

    public void setFrameStatus(String status) {
        this.monsterFrame.setText(status);
    }

    @Override
    public GamePiece[] getGamePieces() {
        return gamePieces;
    }
}
