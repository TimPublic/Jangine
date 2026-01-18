package internal.ecs;


import java.util.HashMap;


/**
 * Manages a specific subclass of {@link ECS_Component}.
 *
 * @param <T> subclass of the component
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class ECS_ComponentSystem<T extends ECS_Component> {


    protected HashMap<Integer, T> _components;


    // -+- CREATION -+- //

    public ECS_ComponentSystem() {
        _components = new HashMap<>();
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
        if (_components.containsKey(id)) {return;}

        component.init(id);

        _components.put(id, component);

        _onComponentAdded(component);
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

        component = _components.get(id);

        if (component == null) {return;}

        component.kill(id);

        _components.remove(id);

        _onComponentRemoved((T) component);
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Is called every frame.
     *
     * @author Tim Kloepper
     */
    public abstract void update(ECS system);


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Called when a {@link ECS_Component} is added.
     *
     * @param component the component that was added
     *
     * @author Tim Kloepper
     */
    protected void _onComponentAdded(T component) {

    }
    /**
     * Called when a {@link ECS_Component} is removed.
     *
     * @param component the component that was removed
     *
     * @author Tim Kloepper
     */
    protected void _onComponentRemoved(T component) {

    }


    // -+- GETTERS -+- //

    public T getComponent(int id) {
        return _components.get(id);
    }


}