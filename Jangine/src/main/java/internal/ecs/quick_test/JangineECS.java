package internal.ecs.quick_test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class JangineECS {


    private final HashMap<Class<? extends JangineECS_Component>, JangineECS_ComponentSystem> _systems;

    private final HashSet<Integer> _activeEntities;

    private Integer _nextEntityID;
    private final ArrayList<Integer> _freeEntityIDs;


    public JangineECS() {
        _systems = new HashMap<>();

        _activeEntities = new HashSet<>();

        _nextEntityID = 0;
        _freeEntityIDs = new ArrayList<>();
    }


    // -+- UPDATE LOOP -+- //

    public final void update() {
        for (JangineECS_ComponentSystem componentSystem : _systems.values()) {
            componentSystem.update();
            componentSystem.updateComponents();
        }
    }


    // -+- ENTITY -+- //

    public final int addEntity() {
        int entityID;

        entityID = _generateEntityID();

        _activeEntities.add(entityID);

        return entityID;
    }
    public final void rmvEntity(int entityID) {
        if (!_activeEntities.contains(entityID)) {return;}

        // Proper kill of entities components.

        _activeEntities.remove(entityID);
        _freeEntityIDs.add(entityID);
    }

    private int _generateEntityID() {
        if (!_freeEntityIDs.isEmpty()) {
            return _freeEntityIDs.get(0);
        }

        return _nextEntityID++; // First returned, then incremented
    }


    // -+- COMPONENT SYSTEM -+- //

    public final void addComponentSystem(Class<? extends JangineECS_Component> componentClass, JangineECS_ComponentSystem system, boolean overwrite) {
        boolean alreadyHasSystem;

        alreadyHasSystem = _systems.containsKey(componentClass);

        if (alreadyHasSystem && !overwrite) {return;}
        if (alreadyHasSystem) {
            // Convert components and kill system properly.
        }

        _systems.put(componentClass, system);
    }
    public final void rmvComponentSystem(Class<? extends JangineECS_Component> componentClass) {
        if (!_systems.containsKey(componentClass)) {return;}

        // Proper kill.
    }

    public JangineECS_ComponentSystem findComponentSystem(Class<? extends JangineECS_Component> componentClass) {
        return _systems.get(componentClass);
    }


    // -+- COMPONENT -+- //

    public final void addComponent(int entityID, JangineECS_Component component) {
        Class<? extends JangineECS_Component> componentClass;

        if (!_activeEntities.contains(entityID)) {return;}

        componentClass = component.getClass();

        if (!_systems.containsKey(componentClass)) {return;}

        _systems.get(componentClass); // Add component to system.
    }
    public final void rmvComponent(int entityID, JangineECS_Component component) {
        Class<? extends JangineECS_Component> componentClass;

        if (!_activeEntities.contains(entityID)) {return;}

        componentClass = component.getClass();

        if (!_systems.containsKey(componentClass)) {return;}

        _systems.get(componentClass); // Remove component from system.
    }


}