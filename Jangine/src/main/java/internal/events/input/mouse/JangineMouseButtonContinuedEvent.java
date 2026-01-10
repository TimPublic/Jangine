package internal.events.input.mouse;


/**
 * This event gets pushed, whenever a mouse-button is continued to be pressed from the previous frame.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineMouseButtonContinuedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonContinuedEvent(int keyCode) {
        super(keyCode);
    }


}
