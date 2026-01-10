package internal.events.input.key;


/**
 * This event is pushed, whenever a key is continued to be pressed,
 * meaning that the key was already pressed in the previous frame.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineKeyContinuedEvent extends JangineKeyEvent {


    public JangineKeyContinuedEvent(int keyCode) {
        super(keyCode);
    }


}
