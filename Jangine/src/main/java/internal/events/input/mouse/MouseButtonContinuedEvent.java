package internal.events.input.mouse;


/**
 * This event gets pushed, whenever a mouse-button is continued to be pressed from the previous frame.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class MouseButtonContinuedEvent extends MouseButtonEvent {


    public MouseButtonContinuedEvent(int keyCode) {
        super(keyCode);
    }


}
