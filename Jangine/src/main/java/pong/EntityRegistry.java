package pong;


import internal.top_classes.A_Entity;

import java.util.HashMap;


public class EntityRegistry {


    // -+- CREATION -+- //

    private EntityRegistry() {
        _ENTITIES_PER_ID = new HashMap<>();
    }

    public static EntityRegistry get() {
        if (_instance == null) _instance = new EntityRegistry();

        return _instance;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private static EntityRegistry _instance;

    // FINALS //

    private final HashMap<Integer, A_Entity> _ENTITIES_PER_ID;


    // -+- ENTITY MANAGEMENT -+- //

    public void add(A_Entity entity) {
        _ENTITIES_PER_ID.put(entity.getId(), entity);
    }
    public void rmv(int id) {
        _ENTITIES_PER_ID.remove(id);
    }


    // -+- GETTERS -+- //

    public A_Entity getEntity(int id) {
        return _ENTITIES_PER_ID.get(id);
    }


}