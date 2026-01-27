package internal.ecs;


import internal.events.EventHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Manages a specific subclass of {@link ECS_Component}.
 *
 * @param <T> subclass of the component
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class A_ComponentSystem<T extends ECS_Component> {


    protected HashMap<Integer, T> p_components;
    protected HashSet<T> p_validComponents;

    protected EventHandler p_eventHandler;


    // -+- CREATION -+- //

    public A_ComponentSystem() {
        p_components = new HashMap<>();
        p_validComponents = new HashSet<>();
    }

    public void init(EventHandler eventHandler) {
        p_eventHandler = eventHandler;
    }


    // -+- ENTITY MANAGEMENT -+- //

    /**
     * Adds an entity with an {@link ECS_Component}.
     *
     * @param id the id of the entity
     * @param component the component that is to be added
     *
     * @author Tim Kloepper
     */
    public void addEntity(int id, T component) {
        if (p_components.containsKey(id)) {return;}

        component.init(id);

        p_components.put(id, component);

        p_onComponentAdded(component);
    }
    /**
     * Removes an entity and therefore the assigned
     * {@link ECS_Component}.
     *
     * @param id the id of the entity
     *
     * @author Tim Kloepper
     */
    public void rmvEntity(int id) {
        ECS_Component component;

        component = p_components.get(id);

        if (component == null) {return;}

        component.kill(id);

        p_components.remove(id);

        p_onComponentRemoved((T) component);
    }


    // -+- UPDATE LOOP -+- //


    public void update(ECS system) {
        for (T component : p_components.values()) {
            if (!component.isActive || !p_isComponentValid(component)) {
                if (p_validComponents.remove(component)) {
                    p_onComponentInvalidated(component);
                }
            }

            if (p_validComponents.add(component)) {
                p_onComponentValidated(component);
            }
        }

        p_internalUpdate(system);
    }
    /**
     * Is called every frame,
     * for you to overwrite.
     *
     * @param system the entity component system that manages this component system
     *
     * @author Tim Kloepper
     */
    protected abstract void p_internalUpdate(ECS system);


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Called when a {@link ECS_Component} is added.
     *
     * @param component the component that was added
     *
     * @author Tim Kloepper
     */
    protected void p_onComponentAdded(T component) {

    }
    /**
     * Called when a {@link ECS_Component} is removed.
     *
     * @param component the component that was removed
     *
     * @author Tim Kloepper
     */
    protected void p_onComponentRemoved(T component) {

    }

    protected void p_onComponentValidated(T component) {}
    protected void p_onComponentInvalidated(T component) {}

    protected abstract boolean p_isComponentValid(T component);


    // -+- CALLBACKS -+- //

    protected abstract void onComponentSystemAdded(A_ComponentSystem componentSystem);
    protected abstract void onComponentSystemRemoved(A_ComponentSystem componentSystem);


    // -+- GETTERS -+- //

    public T getComponent(int id) {
        return p_components.get(id);
    }

    public abstract Collection<Class<? extends ECS_Component>> getRequirements();


}