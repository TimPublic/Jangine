package internal.entity_component_system.specifics.collision.dependencies.calculator;


import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.rendering.container.A_Container;


public interface I_CollisionCalculator {


    // -+- GETTERS -+- //

    A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, ObjectData objB);
    A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, A_Container container);


    // -+- CHECKERS -+- //

    boolean isCollidingWith(ObjectData objA, ObjectData objB);
    boolean isCollidingWith(ObjectData objA, A_Container container);


}