package entity_component_system.specifics.collision.dependencies.calculator;


import entity_component_system.specifics.collision.data.A_CollisionData;
import entity_component_system.specifics.collision.data.ObjectData;
import internal.rendering.container.Container;


public interface I_CollisionCalculator {


    // -+- GETTERS -+- //

    A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, ObjectData objB);
    A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, Container container);


    // -+- CHECKERS -+- //

    boolean isCollidingWith(ObjectData objA, ObjectData objB);
    boolean isCollidingWith(ObjectData objA, Container container);


}