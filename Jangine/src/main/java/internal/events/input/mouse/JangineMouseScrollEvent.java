package internal.events.input.mouse;


/**
 * This event gets pushed, whenever the mouse scrolls.
 * It contains the distance in the x and y direction
 * that the scrolled "moved" this frame.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineMouseScrollEvent extends JangineMouseEvent {


    private final double _SCROLLED_X;
    private final double _SCROLLED_Y;


    public JangineMouseScrollEvent(double scrolledX, double scrolledY) {
        _SCROLLED_X = scrolledX;
        _SCROLLED_Y = scrolledY;
    }


    // -+- GETTERS -+- //

    /**
     * Get the distance the mouse has scrolled in the x direction this frame.
     *
     * @return distance on the x-axis
     *
     * @author Tim Kloepper
     */
    public double getScrolledX() {
        return _SCROLLED_X;
    }

    /**
     * Get the distance the mouse has scrolled in the y direction this frame.
     *
     * @return distance on the y-axis
     *
     * @author Tim Kloepper
     */
    public double getScrollledY() {
        return _SCROLLED_Y;
    }


}