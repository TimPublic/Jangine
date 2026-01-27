package entity_component_system.specifics.position;


import entity_component_system.A_Component;
import org.joml.Vector2d;


public class PositionComponent extends A_Component {


    // -+- CREATION -+- //

    public PositionComponent(Vector2d position) {
        this.position = position;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public Vector2d position;


}