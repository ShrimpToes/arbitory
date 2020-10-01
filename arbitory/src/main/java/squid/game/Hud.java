package squid.game;

import org.joml.Vector4f;
import squid.engine.scene.pieces.GamePiece;
import squid.engine.IHud;
import squid.engine.scene.pieces.TextPiece;
import squid.engine.graphics.textures.FontTexture;

import static squid.engine.Window.getHeight;
import static squid.engine.Window.getWidth;

import java.awt.*;

public class Hud implements IHud {

    private static final String CHARSET = "ISO-8859-1";
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 30);

    private TextPiece textPiece;
    private TextPiece position;
    private GamePiece[] gamePieces;

    public Hud(String text, String text2) throws Exception {
        this.textPiece = new TextPiece(text, new FontTexture(FONT, CHARSET));
        this.textPiece.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        this.textPiece.setRotation(0f, 0f, 0f);
        this.textPiece.setPosition(getWidth() - 300, getHeight() - 40, 0f);

        position = new TextPiece(text2, new FontTexture(FONT, CHARSET));
        position.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        gamePieces = new GamePiece[]{textPiece, position};
    }

    public void setStatusText(String statusText, String position) {
        this.textPiece.setText(statusText);
        this.position.setText(position);
    }

    @Override
    public GamePiece[] getGamePieces() {
        return gamePieces;
    }
}
