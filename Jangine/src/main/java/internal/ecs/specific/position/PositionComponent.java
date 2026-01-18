package internal.ecs.specific.position;


import internal.ecs.ECS_Component;
import org.joml.Vector2d;


public class PositionComponent extends ECS_Component {


    public PositionComponent(double x, double y) {
        position = new Vector2d(x, y);
    }
    public PositionComponent(Vector2d position) {
        this.position = position;
    }


    public Vector2d position;


}