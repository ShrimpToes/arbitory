package squid.game;

import squid.engine.Game;
import squid.game.DummyGame;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {

        new Game("game", 1000, 900, new DummyGame()).start();

    }
}
