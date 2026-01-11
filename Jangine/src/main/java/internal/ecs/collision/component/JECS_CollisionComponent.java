package internal.ecs.collision.component;


import internal.ecs.JangineECS_Component;
import org.joml.Vector2d;


/**
 * Base class for all more specific components.
 * But this base class already contains a position and a width and height.
 * The {@link internal.ecs.collision.help.I_SpatialPartitioning} interface, will
 * only work with those properties, to prevent a type safety dependency injection chaos.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class JECS_CollisionComponent extends JangineECS_Component {


    public JECS_CollisionComponent(Vector2d position, double width, double height) {
        this.position = new Vector2d(position);

        this.width = width;
        this.height = height;
    }


    public Vector2d position;

    public double width, height;


}