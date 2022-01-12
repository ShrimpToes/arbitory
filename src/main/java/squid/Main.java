package squid;

import squid.engine.Game;
import squid.game.DummyGame;
import squid.game.SpaceshipGame;

public class Main {

    public static void main(String[] args) {

        new Game("game", 1000, 900, new SpaceshipGame()).start();

    }
}
