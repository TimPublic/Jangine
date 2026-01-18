import internal.ecs.*;
import internal.ecs.specific.movement.MovementComponent;
import internal.ecs.specific.movement.MovementComponentSystem;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.position.PositionComponentSystem;
import internal.main.Engine;


public class Main {


    public static void main(String[] args) {
        Engine engine;

        engine = Engine.get();

        engine.run();
    }


}