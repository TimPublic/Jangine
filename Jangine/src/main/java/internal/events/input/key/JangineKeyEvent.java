package internal.events.input.key;


import internal.events.JangineEvent;


/**
 * Simple key-event.
 * If this event gets distributed, it means anything has happened with a key.
 * <p>
 * This {@link JangineEvent} subclass gets usually pushed by a {@link internal.input.JangineKeyListener}.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public abstract class JangineKeyEvent extends JangineEvent {


    private final int _KEY_CODE;


    public JangineKeyEvent(int keyCode) {
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