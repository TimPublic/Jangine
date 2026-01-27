package internal.ecs.specific.hitbox;


import internal.ecs.ECS_Component;


public class CircleHitboxComponent extends ECS_Component {


    // -+- CREATION -+- //

    public CircleHitboxComponent(double radius) {
        this.radius = radius;
    }


    // -+- PARAMETERS -+- //

    // NON-FINAL //

    double radius;


}