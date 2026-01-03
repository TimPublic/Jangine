package internal.rendering;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class JangineCamera2D {


    private Matrix4f _projectionMatrix, _viewMatrix;

    public Vector2f _position;


    public JangineCamera2D(int width, int height) {
        _projectionMatrix = new Matrix4f();
        _viewMatrix = new Matrix4f();

        _position = new Vector2f();

        adjustProjection(width, height);
    }


    public JangineCamera2D adjustProjection(int width, int height) {
        _projectionMatrix.identity();

        _projectionMatrix.ortho(0.0f, width * 32.0f, 0.0f, height * 32.0f, 0.0f, 100.0f);

        return this;
    }


    public Matrix4f getViewMatrix() {
        Vector3f cameraFront;
        Vector3f cameraUp;

        cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

        _viewMatrix.identity();
        _viewMatrix.lookAt(
                new Vector3f(_position.x, _position.y, 20.0f),
                new Vector3f(cameraFront.add(_position.x, _position.y, 0.0f)),
                cameraUp
        );

        return _viewMatrix;
    }
    public Matrix4f getProjectionMatrix() {
        return _projectionMatrix;
    }

    public Vector2f getPosition() {
        return _position;
    }


}