package internal.events.input.key;


import internal.events.Event;
import internal.input.KeyListener;


/**
 * Simple key-event.
 * If this event gets distributed, it means anything has happened with a key.
 * <p>
 * This {@link Event} subclass gets usually pushed by a {@link KeyListener}.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public abstract class KeyEvent extends Event {


    private final int _KEY_CODE;


    public KeyEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    // -+- GETTERS -+- //

    /**
     * Returns the code of the key causing this event.
     *
     * @return key-code as an {@link Integer}
     *
     * @author Tim Kloepper
     */
    public int getKeyCode() {
        return _KEY_CODE;
    }


}