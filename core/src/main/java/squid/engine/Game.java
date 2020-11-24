package squid.engine;

import squid.engine.events.Event;
import squid.engine.events.EventRegistry;
import squid.engine.graphics.meshes.MeshBuilder;
import squid.engine.utils.MouseInput;
import squid.engine.Window;
import squid.engine.IGame;

public class Game implements Runnable{

    public static double msPerFrame = 20d;
    public static double msPerTick = 50d;
    public String name;
    private Window window;
    private MouseInput mouseInput;
    private final Thread gameThread;
    private final IGame game;

    public Game(String name, int width, int height, IGame game) {
        this.name = name;
        window = new Window(name, width, height);
        mouseInput = new MouseInput();
        this.game = game;
        gameThread = new Thread(this, name + "_thread");
    }

    private void init() throws Exception {
        window.init();
        mouseInput.init(window);
        game.init();
    }

    protected void input() {
        mouseInput.input(window);
        game.input(window, mouseInput);
    }

    protected void update(float interval) {
        Event.TickEvent tick = new Event.TickEvent();
        tick.currentTimemillis = System.currentTimeMillis();
        tick.intervalmillis = (long) interval;
//        EventRegistry.callEvent(tick); //TODO: fix (nullpointer name cannot be null for thread?
        game.update(interval, mouseInput);
        MeshBuilder.buildMeshes(5);
    }

    protected void render() throws Exception {
        game.render(window);
        window.update();
    }

    protected void cleanup() {
        game.cleanup();
    }

    private void exit() {
        window.exit();
    }

    private boolean shouldExit() {
        return window.shouldExit();
    }

    private void loop() throws Exception {

        double prevTime = System.currentTimeMillis();
        double steps = 0d;

        while (!shouldExit()) {
            double loopStart = System.currentTimeMillis();
            double elapsed = loopStart - prevTime;
            prevTime = System.currentTimeMillis();
            steps += elapsed;

            input();

            while (steps >= msPerTick) {
                update((float) msPerTick);
                steps -= msPerTick;
            }

            render();

//            sync(loopStart, msPerFrame);
        }
    }

    private static void sync(double loopStartTime, double msPerFrame) {
        double endTime = loopStartTime + msPerFrame;
        while(System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {}
        }
    }

    public void start() {
        gameThread.start();
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exit();
            cleanup();
        }
    }
}
