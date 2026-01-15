package internal.ecs.quick_test;


import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;


/**
 * The component system takes over the management of a specified subclass
 * of {@link JangineECS_Component}.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public abstract class JangineECS_ComponentSystem {


    protected final HashMap<Integer, JangineECS_Component> _components;


    private final HashSet<Consumer<JangineECS_Component>> _componentDeletedCallbacks;
    private final HashSet<Consumer<JangineECS_Component>> _componentAddedCallbacks;


    public JangineECS_ComponentSystem() {
        _components = new HashMap<>();

        _componentDeletedCallbacks = new HashSet<>();
        _componentAddedCallbacks = new HashSet<>();
    }


    // -+- LIFE CYCLE -+- //

    /**
     * Called on creation this component system,
     * giving it a brief view of the owning {@link JangineECS}.
     * Component systems should not hold a reference to the system.
     * This parameter should be only used for setup, as finding other
     * component systems for example.
     *
     * @param system owning system
     *
     * @author Tim Kloepper
     */
    public void init(JangineECS system) {}

    /**
     * Called on deleting this component system,
     * giving it a brief view of the owning {@link JangineECS} for
     * removing all setup done in {@link JangineECS_ComponentSystem#init(JangineECS)}.
     * <p>
     * This function should be used to remove all references and connections,
     * to prevent memory leaks.
     *
     * @param system owning system
     *
     * @author Tim Kloepper
     */
    public void kill(JangineECS system) {}


    // -+- UPDATE LOOP -+- //

    /**
     * Called every frame by the owning {@link JangineECS}.
     *
     * @author Tim Kloepper
     */
    public void update() {}

    /**
     * Checks for active {@link JangineECS_Component} and calls {@link JangineECS_ComponentSystem#_onUpdateComponent(JangineECS_Component)}
     * with them as a parameter.
     *
     * @author Tim Kloepper
     */
    public final void updateComponents() {
        for (JangineECS_Component component : _components.values()) {
            if (component.active) {
                _onUpdateComponent(component);
            }
        }

        _onAllComponentsUpdated();
    }

    /**
     * Called for every active {@link JangineECS_Component} every frame.
     *
     * @param ignoredComponent active component. "ignored" is for IntelliJs' intelliSense
     *
     * @author Tim Kloepper
     */
    protected void _onUpdateComponent(JangineECS_Component ignoredComponent) {}
    protected void _onAllComponentsUpdated() {}


    // -+- COMPONENT -+- //

    /**
     * Adds a {@link JangineECS_Component} to the specified entity.
     * If the entity does not exist, a registry will be created for it.
     * But if the entity should already have a component, you have to decide whether to overwrite
     * it or just return.
     *
     * @param entityID id of the entity the specified component should be added to
     * @param component component that should be added to the specified entity
     * @param overwrite whether the possibly already existing component should be overwritten or not
     *
     * @author Tim Kloepper
     */
    public final void addComponent(int entityID, JangineECS_Component component, boolean overwrite) {
        boolean alreadyHasComponentForEntity;

        alreadyHasComponentForEntity = _components.containsKey(entityID);

        if (alreadyHasComponentForEntity && !overwrite) {return;}
        if (alreadyHasComponentForEntity) {
            _components.get(entityID).kill(entityID);
        }

        component.init(entityID);

        _components.put(entityID, component);

        _onAddComponent(component);

        for (Consumer<JangineECS_Component> callback : _componentAddedCallbacks) {
            callback.accept(component);
        }
    }

    /**
     * Removes a {@link internal.ecs.JangineECS_Component} from the specified entity,
     * removing the entity from this component system completely.
     * If component or entity should not exist, nothing happens.
     *
     * @param entityID id of the entity the specified component should be removed from
     * @param component component that is to be removed from the specified entity
     *
     * @author Tim Kloepper
     */
    public final void rmvComponent(int entityID, JangineECS_Component component) {
        if (!_components.containsKey(entityID)) {return;}
        if (_components.get(entityID) != component) {return;}

        component.kill(entityID);

        _components.remove(entityID);

        _onRmvComponent(component);

        for (Consumer<JangineECS_Component> callback : _componentDeletedCallbacks) {
            callback.accept(component);
        }
    }

    /**
     * Called after a {@link JangineECS_Component} has been added.
     * But at this point, the {@link JangineECS_Component#init(int)} method has already been
     * called on the specified component.
     *
     * @param component component that was added
     *
     * @author Tim Kloepper
     */
    protected void _onAddComponent(JangineECS_Component component) {}

    /**
     * Called after a {@link JangineECS_Component} has been removed.
     * But at this point, the {@link JangineECS_Component#kill(int)} method has already been
     * called on the specified component.
     *
     * @param component component that was removed
     *
     * @author Tim Kloepper
     */
    protected void _onRmvComponent(JangineECS_Component component) {}

    public void registerComponentAddedCallback(Consumer<JangineECS_Component> callback) {
        _componentAddedCallbacks.add(callback);
    }
    public void registerComponentDeletedCallback(Consumer<JangineECS_Component> callback) {
        _componentDeletedCallbacks.add(callback);
    }


    // -+- GETTERS -+- //

    public JangineECS_Component getComponentByEntity(int entityID) {
        return _components.get(entityID);
    }


}