package internal.events;


public class JangineMouseButtonEvent {


    private final int _KEY_CODE;


    public JangineMouseButtonEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    public int getKeyCode() {
        return _KEY_CODE;
    }


}


class JangineMouseButtonPressedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonPressedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineMouseButtonReleasedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonReleasedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineMouseButtonContinuedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonContinuedEvent(int keyCode) {
        super(keyCode);
    }


}