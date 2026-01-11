package internal.ecs;


import internal.ecs.components.implementations.JangineECS_Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * This singleton class, accessible through the respective {@link JangineECS_System#get()} method,
 * manages and updates all {@link JangineECS_Component} active.
 * But it does not care about killing the {@link JangineECS_Component} upon removal (see {@link JangineECS_System#rmvComponents(List)}).
 * This enables the system of "deactivating" entities or rather components and "activating" them again later on.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineECS_System {


    private HashSet<JangineECS_Component> _components;


    private static JangineECS_System _instance;


    private JangineECS_System() {
        _components = new HashSet<>();
    }

    /**
     * Returns the singleton instance of this system.
     *
     * @return the singleton instance of this system.
     *
     * @author Tim Kloepper
     */
    public static JangineECS_System get() {
        if (_instance == null) {
            _instance = new JangineECS_System();
        }

        return _instance;
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Called every frame.
     * Updates all registered {@link JangineECS_Component}.
     *
     * @param deltaTime time passed since the last frame
     *
     * @author Tim Kloepper
     */
    public final void update(double deltaTime) {
        for (JangineECS_Component component : _components) {
            component.update(deltaTime);
        }
    }


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Adds components to the system.
     * These components need to be initialized, which typically
     * happens when adding the components to a {@link JangineEntity}.
     * If this condition is not met, the engine will crash.
     *
     * @param components components to be added
     *
     * @author Tim Kloepper
     */
    public void addComponents(Collection<JangineECS_Component> components) {
        components.forEach(this::addComponent);
    }
    /**
     * Adds a component to the system.
     * This component needs to be initialized, which typically
     * happens when adding a component to a {@link JangineEntity}.
     * If this condition is not met, the engine will crash.
     *
     * @param component component to be added
     *
     * @author Tim Kloepper
     */
    public void addComponent(JangineECS_Component component) {
        if (!component.hasBeenInitialized()) { // <- Has not been added by an entity.
            System.err.println("[ECS ERROR] : Tried to add not initialized component!");
            System.err.println("|-> Component : " + component);

            System.exit(1);
        }

        _components.add(component);
    }
    /**
     * Removes components from the system.
     * The system does not call {@link JangineECS_Component#kill(JangineEntity)} on them.
     * If you want to delete them because of the deletion of the {@link JangineEntity} that owns them,
     * the {@link JangineEntity#kill()} method, already takes care of that.
     *
     * @param components components to be deleted
     *
     * @author Tim Kloepper
     */
    public void rmvComponents(Collection<JangineECS_Component> components) {
        components.forEach(_components::remove); // IntelliJ suggestion
    }
    /**
     * Removes a component from the system.
     * The system does not call {@link JangineECS_Component#kill(JangineEntity)} on it.
     * If you want to delete it because of the deletion of the {@link JangineEntity} that owns it,
     * the {@link JangineEntity#kill()} method, already takes care of that.
     *
     * @param component component to be deleted
     *
     * @author Tim Kloepper
     */
    public void rmvComponent(JangineECS_Component component) {
        _components.remove(component);
    }


}