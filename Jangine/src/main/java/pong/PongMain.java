package pong;


import internal.main.Engine;
import internal.rendering.container.Window;


public class PongMain {


    public static void main(String[] args) {
        Engine engine;

        engine = Engine.get();

        Window window;

        window = new Window(1920, 1080);

        PongScene scene;

        scene = new PongScene();

        window.addAndActivateScene(scene);

        engine.addWindow(window);

        engine.run();
    }


}