package internal.main;

import internal.rendering.JangineWindow;

public class Engine {


    private static Engine _instance;


    private boolean _shouldClose;


    private Engine() {
        _shouldClose = false;
    }

    public static Engine get() {
        if (_instance == null) {
            _instance = new Engine();
        }

        return _instance;
    }


    public void run() {
        JangineWindow window;

        window = JangineWindow.get();

        while (!_shouldClose) {
            _shouldClose = !window.update();
        }
    }


}