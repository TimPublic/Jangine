package internal.events.input.key;


/**
 * This event gets pushed, whenever a key gets released,
 * meaning it was pressed in the previous frame, but is
 * not pressed in this frame anymore.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineKeyReleasedEvent extends JangineKeyEvent {


    public JangineKeyReleasedEvent(int keyCode) {
        super(keyCode);
    }


}