package internal.ecs.specific.hitbox;


import internal.ecs.ECS_Component;


public class RectangleHitboxComponent extends ECS_Component {


    // -+- CREATION -+- //

    public RectangleHitboxComponent(double width, double height) {
        this.width = width;
        this.height = height;
    }


    // -+- PARAMETERS -+- //

    // NON-FINAL //

    public double width, height;


}