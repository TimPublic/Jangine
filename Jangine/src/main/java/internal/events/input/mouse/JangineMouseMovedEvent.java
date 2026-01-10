package internal.events.input.mouse;


/**
 * This event gets pushed, whenever the mouse moves over the screen.
 * It contains the previous position on x and y and the new position on x and y.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineMouseMovedEvent extends JangineMouseEvent {


    private final double _PREV_X, _PREV_Y;
    private final double _NEW_X, _NEW_Y;


    public JangineMouseMovedEvent(double prevX, double prevY, double newX, double newY) {
        _PREV_X = prevX;
        _PREV_Y = prevY;

        _NEW_X = newX;
        _NEW_Y = newY;
    }


    // -+- GETTERS -+- //

    /**
     * Get the previous position of the mouse on the x-axis.
     *
     * @return previous position of the mouse on the x-axis.
     *
     * @author Tim Kloepper
     */
    public double getPrevX() {
        return _PREV_X;
    }
    /**
     * Get the previous position of the mouse on the y-axis.
     *
     * @return previous position of the mouse on the y-axis.
     *
     * @author Tim Kloepper
     */
    public double getPrevY() {
        return _PREV_Y;
    }

    /**
     * Get the new position of the mouse on the x-axis.
     *
     * @return new position of the mouse on the x-axis.
     *
     * @author Tim Kloepper
     */
    public double getNewX() {
        return _NEW_X;
    }
    /**
     * Get the new position of the mouse on the y-axis.
     *
     * @return new position of the mouse on the y-axis.
     *
     * @author Tim Kloepper
     */
    public double getNewY() {
        return _NEW_Y;
    }

    /**
     * Get the difference between the new and old position of the mouse on the x-axis.
     *
     * @return difference on the x-axis.
     *
     * @author Tim Kloepper
     */
    public double getDeltaX() {
        return _PREV_X - _NEW_Y;
    }
    /**
     * Get the difference between the new and old position of the mouse on the y-axis.
     *
     * @return difference on the y-axis.
     *
     * @author Tim Kloepper
     */
    public double getDeltaY() {
        return _PREV_Y - _NEW_Y;
    }


}