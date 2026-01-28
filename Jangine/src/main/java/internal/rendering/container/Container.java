package internal.rendering.container;


import org.joml.Vector2d;


public class Container {


    private Vector2d _position;
    private double _width, _height;


    // -+- GETTERS -+- //

    public Vector2d getPosition() {
        return _position;
    }
    public double getWidth() {
        return _width;
    }
    public double getHeight() {
        return _height;
    }


}