package internal.events.input.mouse;

public class JangineMouseScrollEvent extends JangineMouseEvent {


    private final double _SCROLLED_X;
    private final double _SCROLLED_Y;


    public JangineMouseScrollEvent(double scrolledX, double scrolledY) {
        _SCROLLED_X = scrolledX;
        _SCROLLED_Y = scrolledY;
    }


    public double getScrolledX() {
        return _SCROLLED_X;
    }
    public double get_SCROLLED_Y() {
        return _SCROLLED_Y;
    }


}
