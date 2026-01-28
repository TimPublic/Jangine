package internal.entity_component_system;


import internal.rendering.container.Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public abstract class A_Processor<T extends A_Component> {


    // -+- CREATION -+- //

    public A_Processor() {
        _components = new HashMap<>();
    }

    protected abstract void p_init(System system, Scene scene);
    protected abstract void p_kill(System system, Scene scene);

    protected abstract void p_receiveRequiredProcessors(HashMap<Class<? extends T>, A_Processor<?>> requiredProcessors);


    // -+- PARAMETERS -+- //

    // FINALS //

    protected final HashMap<Integer, T> _components;


    // -+- UPDATE LOOP -+- //

    /**
     * The update method that is always the same and called by the system.
     * It creates a list of valid components and pushes them,
     * together with the system itself and the owning scene to the internal
     * update method.
     *
     * @param system System that manages this processor
     * @param scene Scene that owns the specified system
     *
     * @author Tim Kloepper
     */
    protected final void p_update(System system, Scene scene) {
        HashSet<T> validComponents;

        validComponents = new HashSet<>();

        for (T component : _components.values()) {
            if (!component.active) continue;
            if (!p_isComponentValid(component)) continue;

            validComponents.add(component);
        }

        p_internalUpdate(validComponents, system, scene);
    }
    protected abstract void p_internalUpdate(Collection<T> validComponents, System system, Scene scene);


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Adds a component to the processor, as
     * long as the component is valid,
     * specified by the {@link A_Processor#p_isComponentValid(A_Component)}
     * method.
     * If the entity already has a component assigned to it,
     * {@code false} is returned, unless {@code overwrite}
     * is set to true.
     *
     * @param entityId The id of the entity that the specified component should be added to
     * @param component Component that is to be added
     * @param overwrite Determines whether a possibly already existing component should be overwritten
     *
     * @return success
     */
    public boolean addComponent(int entityId, T component, boolean overwrite) {
        if (!p_isComponentValid(component)) return false;
        if (_components.containsKey(entityId)) return false;

        _components.put(entityId, component);

        return true;
    }
    /**
     * Removes a component from the processor,
     * based on the entity id.
     *
     * @return The component that got removed,
     * or {@code null} if no component was mapped
     * to the specified entity id
     *
     * @author Tim Kloepper
     */
    public T rmvComponent(int entityId) {
        if (!_components.containsKey(entityId)) return null;

        return _components.remove(entityId);
    }

    /**
     * A way to define custom checks for the systems,
     * to make sure, components that are passed to the internal update
     * method are valid.
     *
     * @param component Component that is to be checked
     *
     * @return Whether the component is valid or not
     *
     * @author Tim Kloepper
     */
    protected abstract boolean p_isComponentValid(T component);


    // -+- GETTERS -+- //

    /**
     * Returns the classes of the components this processor would like to process.
     * By doing this over an abstract method, no unnecessary overhead
     * is required by the user upon adding a processor.
     * How the event, of components in this collection already being processed,
     * is handled, is a matter of the system.
     *
     * @return the classes of the components this processor would like to process.
     *
     * @author Tim Kloepper
     */
    protected abstract Collection<Class<? extends T>> p_getProcessedComponentClasses();
    /**
     * Returns the classes of the components this processor relies on, but does not manage.
     * This method ensures, the processor can work properly.
     * How the event, of requirements not met, is handled,
     * is a matter of the system.
     *
     * @return the classes of the components this processor requires.
     *
     * @author Tim Kloepper
     */
    protected abstract Collection<Class<? extends A_Component>> p_getRequiredComponentClasses();

    public T getComponent(int entityId) {
        return _components.get(entityId);
    }


    // -+- CHECKERS -+- //

    public boolean hasEntity(int entityId) {
        return _components.containsKey(entityId);
    }


}