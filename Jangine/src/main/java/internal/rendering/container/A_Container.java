package internal.rendering.container;


import org.joml.Vector2d;


public class A_Container {


    public A_Container(Vector2d position, double width, double height) {
        _position = position;
        _width = width;
        _height = height;
    }


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