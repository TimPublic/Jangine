package internal.ecs.quick_test.movement;


import internal.ecs.quick_test.JangineECS_Component;
import org.joml.Vector2d;


public class JangineECS_MovementComponent extends JangineECS_Component {


    public JangineECS_MovementComponent(Vector2d direction, int speed) {
        this.direction = direction;

        this.speed = speed;
    }


    public Vector2d direction;

    public int speed;


}