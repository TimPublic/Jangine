package internal.ecs.quick_test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * The entity component system provides a lightweight and flexible way,
 * of assigning behaviour to entities.
 * You can easily add new entities, components or component systems.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
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

    /**
     * Adds an entity to the system.
     * As the entity is just a number with assigned {@link internal.ecs.quick_test.JangineECS_Component}
     * a number is returned, which represents that integer.
     *
     * @return the id of the entity
     *
     * @author Tim Kloepper
     */
    public final int addEntity() {
        int entityID;

        entityID = _generateEntityID();

        return entityID;
    }

    /**
     * Removes an entity from the system.
     * If the provided entity id is not valid, nothing happens.
     *
     * @param entityID the id of the entity that is to be removed
     *
     * @author Tim Kloepper
     */
    public final void rmvEntity(int entityID) {
        if (!_activeEntities.contains(entityID)) {return;}

        // Proper kill of entities components.

        _activeEntities.remove(entityID);
        _freeEntityIDs.add(entityID);
    }

    /**
     * Takes a free id and returns it to be used as a new entity id.
     * By storing freed ids, the numbers do not grow too large.
     * This method adds the id to the active entities and removes it
     * from the freed ids, if necessary.
     *
     * @return free entity id you can use
     *
     * @author Tim Kloepper
     */
    private int _generateEntityID() {
        int id;

        if (!_freeEntityIDs.isEmpty()) {
            id = _freeEntityIDs.get(0);

            _freeEntityIDs.remove(id);
        } else {
            id = _nextEntityID++; // First returned, then incremented.
        }

        _activeEntities.add(id);

        return id;
    }


    // -+- COMPONENT SYSTEM -+- //

    /**
     * Adds a {@link JangineECS_ComponentSystem} to the system.
     * You have to specify what to do in case of a system already being registered
     * for your provided {@link JangineECS_Component} subclass:
     * overwrite or return.
     *
     * @param componentClass class the component system is to be registered for
     * @param system the component system that is to be registered
     * @param overwrite whether a possible old component system should be overwritten or not
     *
     * @author Tim Kloepper
     */
    public final void addComponentSystem(Class<? extends JangineECS_Component> componentClass, JangineECS_ComponentSystem system, boolean overwrite) {
        boolean alreadyHasSystem;
        JangineECS_ComponentSystem oldSystem;

        alreadyHasSystem = _systems.containsKey(componentClass);

        if (alreadyHasSystem && !overwrite) {return;}
        if (alreadyHasSystem) {
            oldSystem = _systems.get(componentClass);

            for (int entityID : oldSystem._components.keySet()) {
                oldSystem.rmvComponent(entityID, oldSystem._components.get(entityID));
                system.addComponent(entityID, oldSystem._components.get(entityID), false);
            }

            oldSystem.kill(this);
        }

        system.init(this);
        _systems.put(componentClass, system);
    }
    /**
     * Removes a {@link JangineECS_ComponentSystem} by specifying the {@link JangineECS_Component} subclass
     * it is registered for.
     * If no component system is registered for that subclass, nothing happens.
     *
     * @param componentClass subclass that the component system is registered for
     *
     * @author Tim Kloepper
     */
    public final void rmvComponentSystem(Class<? extends JangineECS_Component> componentClass) {
        JangineECS_ComponentSystem system;

        if (!_systems.containsKey(componentClass)) {return;}

        system = _systems.get(componentClass);

        system.kill(this);
        _systems.remove(componentClass);
    }

    /**
     * Finds a {@link JangineECS_ComponentSystem} based on the {@link JangineECS_Component} subclass it is registered for.
     * If the is no component system registered for that subclass, null is returned.
     *
     * @param componentClass subclass the searched component system is registered for
     *
     * @return the component system
     *
     * @author Tim Kloepper
     */
    public JangineECS_ComponentSystem findComponentSystem(Class<? extends JangineECS_Component> componentClass) {
        return _systems.get(componentClass);
    }


    // -+- COMPONENT -+- //

    /**
     * Adds a {@link JangineECS_Component} to the specified entity.
     * If the entity does not exist, or the entity already contains a component of that class,
     * nothing happens.
     *
     * @param entityID id of the entity the component is to be added to
     * @param component component that is to be added to the specified entity
     *
     * @author Tim Kloepper
     */
    public final void addComponent(int entityID, JangineECS_Component component) {
        Class<? extends JangineECS_Component> componentClass;

        if (!_activeEntities.contains(entityID)) {return;}

        componentClass = component.getClass();

        if (!_systems.containsKey(componentClass)) {return;}

        _systems.get(componentClass); // Add component to system.
    }

    /**
     * Removes a {@link JangineECS_Component} to the specified entity.
     * If the entity does not exist, or the component is not registered for that entity,
     * nothing happens.
     *
     * @param entityID id of the entity the component is to be removed from
     * @param component component that is to be removed from the specified entity
     */
    public final void rmvComponent(int entityID, JangineECS_Component component) {
        Class<? extends JangineECS_Component> componentClass;

        if (component.entityID != entityID) {return;} // Quick and easy check. If this fails, we can spare ourselves of any other checks.

        if (!_activeEntities.contains(entityID)) {return;}

        componentClass = component.getClass();

        if (!_systems.containsKey(componentClass)) {return;}

        _systems.get(componentClass); // Remove component from system.
    }


    // -+- CHECKERS -+- //

    public boolean hasEntity(int entityID) {
        return _activeEntities.contains(entityID);
    }


}