package internal.entity_component_system.specifics.velocity;


import internal.entity_component_system.A_Component;
import org.joml.Vector2d;


public class VelocityComponent extends A_Component {


    // -+- CREATION -+- //

    public VelocityComponent(Vector2d velocity, int mult) {
        super();

        VELOCITY = velocity;
        this.mult = mult;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final Vector2d VELOCITY;

    // NON-FINALS //

    public int mult;


}