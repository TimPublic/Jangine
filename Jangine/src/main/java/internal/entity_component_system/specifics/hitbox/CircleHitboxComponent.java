package internal.entity_component_system.specifics.hitbox;


public class CircleHitboxComponent extends A_HitboxComponent {


    // -+- CREATION -+- //

    public CircleHitboxComponent(double radius) {
        this.radius = radius;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public double radius;


}