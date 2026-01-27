package internal.entity_component_system.specifics.collision.data;


import internal.entity_component_system.specifics.hitbox.A_HitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;


public class ObjectData {


    public ObjectData(A_HitboxComponent hitboxComponent, PositionComponent positionComponent) {
        this.hitboxComponent = hitboxComponent;
        this.positionComponent = positionComponent;
    }


    public A_HitboxComponent hitboxComponent;
    public PositionComponent positionComponent;


}