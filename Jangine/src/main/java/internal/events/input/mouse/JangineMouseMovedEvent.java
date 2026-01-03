package internal.events.input.mouse;

public class JangineMouseMovedEvent extends JangineMouseEvent {


    private final double _PREV_X, _PREV_Y;
    private final double _NEW_X, _NEW_Y;


    public JangineMouseMovedEvent(double prevX, double prevY, double newX, double newY) {
        _PREV_X = prevX;
        _PREV_Y = prevY;

        _NEW_X = newX;
        _NEW_Y = newY;
    }


    public double getPrevX() {
        return _PREV_X;
    }
    public double getPrevY() {
        return _PREV_Y;
    }

    public double getNewX() {
        return _NEW_X;
    }
    public double getNewY() {
        return _NEW_Y;
    }

    public double getDeltaX() {
        return _PREV_X - _NEW_Y;
    }
    public double getDeltaY() {
        return _PREV_Y - _NEW_Y;
    }


}
