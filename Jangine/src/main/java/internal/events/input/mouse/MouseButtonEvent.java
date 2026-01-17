package internal.events.input.mouse;


/**
 * This event gets pushed, whenever anything happens with a mouse-button.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public abstract class MouseButtonEvent extends MouseEvent {


    private final int _KEY_CODE;


    public MouseButtonEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    public int getKeyCode() {
        return _KEY_CODE;
    }


}
