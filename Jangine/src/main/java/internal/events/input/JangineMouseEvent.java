package internal.events.input;


import internal.events.JangineEvent;


public class JangineMouseEvent extends JangineEvent {}


class JangineMouseMovedEvent extends JangineMouseEvent {


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
class JangineMouseScrollEvent extends JangineMouseEvent {


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

abstract class JangineMouseButtonEvent extends JangineMouseEvent {


    private final int _KEY_CODE;


    public JangineMouseButtonEvent(int keyCode) {
        _KEY_CODE = keyCode;
    }


    public int getKeyCode() {
        return _KEY_CODE;
    }


}
class JangineMouseButtonPressedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonPressedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineMouseButtonContinuedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonContinuedEvent(int keyCode) {
        super(keyCode);
    }


}
class JangineMouseButtonReleasedEvent extends JangineMouseButtonEvent {


    public JangineMouseButtonReleasedEvent(int keyCode) {
        super(keyCode);
    }


}

abstract class JangineMouseDraggingEvent extends JangineMouseEvent {}
class JanigneMouseDraggingStartedEvent extends JangineMouseDraggingEvent {}
class JangineMouseDraggingEndedEvent extends JangineMouseDraggingEvent {}