package internal.rendering;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


/**
 * Specifies the viewport used in rendering.
 * <p>
 * This is a temporary class, which weill be refactored in the future to
 * be more efficient and better integrated.
 * Currently, it is a nearly one-on-one copy from "Games with Gabe".
 *
 * @author Tim Klöpper
 * @version 0.9
 */
public class Camera2D {


    private Matrix4f _projectionMatrix, _viewMatrix;

    public Vector2f _position;


    public Camera2D(int width, int height) {
        _projectionMatrix = new Matrix4f();
        _viewMatrix = new Matrix4f();

        _position = new Vector2f();

        adjustProjection(width, height);
    }


    // -+- PROJECTION-LOGIC -+- //

    /**
     * Adjust how much this camera can see in 32-by-32 pixels.
     *
     * @param width
     * @param height
     *
     * @return this, for linked calls.
     *
     * @author Tim Kloepper
     */
    public Camera2D adjustProjection(int width, int height) {
        _projectionMatrix.identity();

        _projectionMatrix.ortho(0.0f, width * 32.0f, 0.0f, height * 32.0f, 0.0f, 100.0f);

        return this;
    }


    // -+- GETTERS -+- //

    /**
     * Returns the view matrix.
     *
     * @return view matrix
     *
     * @author Tim Kloepper
     */
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
    /**
     * Returns the projection matrix.
     *
     * @return projection matrix
     *
     * @author Tim Kloepper
     */
    public Matrix4f getProjectionMatrix() {
        return _projectionMatrix;
    }

    /**
     * Returns the current position of this camera.
     *
     * @return position
     *
     * @author Tim Kloepper
     */
    public Vector2f getPosition() {
        return _position;
    }


}