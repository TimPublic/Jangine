package entity_component_system.specifics.hitbox;


public class RectangleHitboxComponent extends A_HitboxComponent {


    // -+- CREATION -+- //

    public RectangleHitboxComponent(double width, double height) {
        this.width = width;
        this.height = height;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public double width, height;


}