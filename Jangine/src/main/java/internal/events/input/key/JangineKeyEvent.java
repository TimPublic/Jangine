package internal.events.input.key;


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