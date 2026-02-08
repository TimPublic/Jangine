package internal.entity_component_system.specifics.velocity;


import internal.entity_component_system.A_Component;
import org.joml.Vector2d;


public class VelocityComponent extends A_Component {


    // -+- CREATION -+- //

    public VelocityComponent(Vector2d velocity) {
        super();

        VELOCITY = velocity;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final Vector2d VELOCITY;


}