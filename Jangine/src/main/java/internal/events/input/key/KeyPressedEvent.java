package internal.events.input.key;


/**
 * This event gets pushed, whenever a key gets pressed,
 * meaning in the previous frame it was not.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class KeyPressedEvent extends KeyEvent {


    public KeyPressedEvent(int keyCode) {
        super(keyCode);
    }


}