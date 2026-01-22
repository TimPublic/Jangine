package internal.ecs;


import internal.rendering.container.Scene;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Manages the entities and their components.
 * The entities are just numbers and the components
 * are {@link ECS_Component}, held by {@link ECS_ComponentSystem}.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class ECS {


    private final HashSet<Integer> _activeEntities;
    private int _nextEntityIndex;

    private final HashMap<Class<? extends ECS_Component>, ECS_ComponentSystem<? extends ECS_Component>> _componentSystems;

    private final Scene _scene;

    private final HashMap<Class<? extends ECS_Component>, ECS_ComponentSystem<?>> _bufferedSystems;


    // -+- CREATION -+- //

    public ECS(Scene scene) {
        _activeEntities = new HashSet<>();
        _nextEntityIndex = 0;

        _componentSystems = new HashMap<>();

        _scene = scene;

        _bufferedSystems = new HashMap<>();
    }


    // -+- ENTITY MANAGEMENT -+- //

    /**
     * Adds an entity and returns the id of the created entity.
     *
     * @return the id of the created entity
     *
     * @author Tim Kloepper
     */
    public int addEntity() {
        int id;

        id = _nextEntityIndex++;
        _activeEntities.add(id);

        return id;
    }
    /**
     * Removes an entity.
     * If the entity does not exist,
     * nothing happens.
     *
     * @param id the id of the entity that is to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvEntity(int id) {
        if (!_activeEntities.remove(id)) {return;}

        for (ECS_ComponentSystem<? extends ECS_Component> system : _componentSystems.values()) {
            system.rmvEntity(id);
        }
    }


    // -+- COMPONENT SYSTEM MANAGEMENT -+- //

    /**
     * Adds an {@link ECS_ComponentSystem} to the system.
     * You also need to specify the subclass of the
     * {@link ECS_Component} that will be managed by the
     * specified component system.
     *
     * @param componentSystem component system to be added
     * @param componentClass subclass of the component that is to be managed by the component system
     */
    public boolean addComponentSystem(ECS_ComponentSystem<? extends ECS_Component> componentSystem, Class<? extends ECS_Component> componentClass, boolean overwrite) {
        if (componentSystem == null) {return false;}
        if (_componentSystems.containsKey(componentClass) && !overwrite) {return false;}
        // Check requirements
        for (Class<? extends ECS_Component> requiredClass : componentSystem.getRequirements()) {
            if (!_componentSystems.containsKey(requiredClass)) {return false;}
        }

        componentSystem.init(_scene.getEventHandler());

        _componentSystems.put(componentClass, componentSystem);

        for (ECS_ComponentSystem system : _componentSystems.values()) {
            system.onComponentSystemAdded(componentSystem);
        }

        return true;
    }
    /**
     * Removes an {@link ECS_ComponentSystem} based on the subclass of the {@link ECS_Component}
     * it manages.
     *
     * @param componentClass subclass of the component that is managed by the component system that is to be removed
     *
     * @author Tim Kloepper
     */
    public boolean rmvComponentSystem(Class<? extends ECS_Component> componentClass) {
        if (!_componentSystems.containsKey(componentClass)) {return false;}

        ECS_ComponentSystem<?> componentSystem;

        componentSystem = _componentSystems.get(componentClass);
        if (componentSystem == null) {return false;}

        for (ECS_ComponentSystem<?> system : _componentSystems.values()) {
            system.onComponentSystemRemoved(componentSystem);
        }

        _componentSystems.remove(componentClass);

        return true;
    }


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Adds a component to the specified entity.
     * If the entity does not exist,
     * nothing happens.
     *
     * @param id the id of the entity that the specified component is to be added to
     * @param component the component that is to be added to the specified entity
     *
     * @author Tim Kloepper
     */
    public void addComponent(int id, ECS_Component component) {
        ECS_ComponentSystem componentSystem;

        componentSystem = _componentSystems.get(component.getClass());

        if (componentSystem == null) {return;}

        componentSystem.addEntity(id, component);
    }
    /**
     * Removes a component from the specified entity.
     * If the entity does not exist,
     * nothing happens.
     *
     * @param id the id of the entity that the specified component is to be removed from.
     * @param component the component that is to be removed from the specified entity
     *
     * @author Tim Kloepper
     */
    public void rmvComponent(int id, ECS_Component component) {
        ECS_ComponentSystem<? extends ECS_Component> componentSystem;

        componentSystem = _componentSystems.get(component.getClass());

        if (componentSystem == null) {return;}

        componentSystem.rmvEntity(id);
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Updates all the component systems.
     *
     * @author Tim Kloepper
     */
    public void update() {
        for (ECS_ComponentSystem<? extends ECS_Component> componentSystem : _componentSystems.values()) {
            componentSystem.update(this);
        }
    }


    // -+- GETTERS -+- //

    public ECS_ComponentSystem<? extends ECS_Component> getComponentSystem(Class<? extends ECS_Component> componentClass) {
        return _componentSystems.get(componentClass);
    }


}