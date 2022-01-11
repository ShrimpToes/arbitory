package squid.engine.events;

import squid.engine.Window;
import squid.engine.scene.Scene;
import squid.engine.utils.Camera;
import squid.engine.IHud;

public abstract class Event {
    protected String name = "unnamedevent";
    public long currentTimemillis;


    public static class TickEvent extends Event {
        public String name = "tickevent";
        public long intervalmillis;
    }

    public static class RenderEvent extends Event {
        public String name = "renderevent";
        public Scene scene;
        public Window window;
        public Camera camera;
        public IHud hud;
    }

}
