package internal.main;

import internal.events.JangineEventHandler;
import internal.rendering.JangineWindow;

public class Engine {


    private static Engine _instance;


    private JangineEventHandler _eventHandler;

    private boolean _shouldClose;


    private Engine() {
        _eventHandler = new JangineEventHandler();

        _shouldClose = false;
    }

    public static Engine get() {
        if (_instance == null) {
            _instance = new Engine();
        }

        return _instance;
    }


    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }

    public void run() {
        JangineWindow window;
        JangineWindow secondWindow;

        window = new JangineWindow(this);
        secondWindow = new JangineWindow(this);

        while (!_shouldClose) {
            _shouldClose = !window.update();

            secondWindow.update();
        }
    }


}