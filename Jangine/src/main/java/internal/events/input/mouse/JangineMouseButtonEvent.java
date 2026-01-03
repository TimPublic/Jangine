package internal.events.input.mouse;

public abstract class JangineMouseButtonEvent extends JangineMouseEvent {


    private final int _KEY_CODE;


    public JangineMouseButtonEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    public int getKeyCode() {
        return _KEY_CODE;
    }


}
