package squid.engine;

import squid.engine.utils.MouseInput;

public interface IGame {
    void init() throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window) throws Exception;

    void cleanup();
}
