package internal.entity_component_system.specifics.collision.dependencies.spatial_partitioner;


import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.rendering.container.A_Container;

import java.util.Collection;


public interface I_SpatialPartitioner {


    // -+- UPDATE LOOP -+- //

    void update(A_Container container);


    // -+- ADDITION AND REMOVAL -+- //

    void addObject(ObjectData obj);
    void rmvObject(ObjectData obj);


    // -+- GETTERS -+- //

    Collection<ObjectData> getCollidingObjects(ObjectData obj);


}