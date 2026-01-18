package internal.ecs.specific.movement;


import internal.ecs.ECS_Component;
import org.joml.Vector2d;


public class MovementComponent extends ECS_Component {


    public MovementComponent(double x, double y, double speed) {
        direction = new Vector2d(x, y);
        this.speed = speed;
    }
    public MovementComponent(Vector2d direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }


    public Vector2d direction;
    public double speed;


}