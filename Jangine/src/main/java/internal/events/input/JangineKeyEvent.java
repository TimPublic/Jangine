package internal.events.input;


import internal.events.JangineEvent;


public abstract class JangineKeyEvent extends JangineEvent {


    private final int _KEY_CODE;


    public JangineKeyEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    public int getKeyCode() {
        return _KEY_CODE;
    }


}


class JangineKeyPressedEvent extends JangineKeyEvent {


    public JangineKeyPressedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineKeyContinuedEvent extends JangineKeyEvent {


    public JangineKeyContinuedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineKeyReleasedEvent extends JangineKeyEvent {


    public JangineKeyReleasedEvent(int keyCode) {
        super(keyCode);
    }


}