package internal.ecs;


import internal.ecs.components.implementations.JangineECS_Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * An entity is made out of {@link JangineECS_Component} that are updated every frame by {@link JangineECS_System}.
 * The entity also contains a {@link HashSet} of ids, them being integers, for quick identification,
 * for example of a player.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineEntity {


    private HashSet<JangineECS_Component> _components;
    private HashSet<Integer> _ids;


    public JangineEntity(List<Integer> ids) {
        _components = new HashSet<>();
        _ids = new HashSet<>(ids);
    }


    // -+- ID MANAGEMENT -+- //

    /**
     * Adds an id to the entity.
     *
     * @param id new id
     *
     * @author Tim Kloepper
     */
    public void addID(int id) {
        _ids.add(id);
    }

    /**
     * Removes an id from the entity.
     * If the id does not exist, nothing happens.
     *
     * @param id id to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvID(int id) {
        _ids.remove(id);
    }


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Adds a {@link Collection} {@link JangineECS_Component} to the entity.
     *
     * @param components components to be added
     *
     * @author Tim Kloepper
     */
    public void addComponents(Collection<JangineECS_Component> components) {
        components.forEach(this::addComponent);
    }
    /**
     * Adds a {@link JangineECS_Component} to the entity.
     *
     * @param component component to be added
     *
     * @author Tim Kloepper
     */
    public void addComponent(JangineECS_Component component) { // Thought about just passing the class, but if the subclass needs parameters this makes thing unnecessarily complicated.
        component.init(this);

        JangineECS_System.get().addComponent(component);
    }
    /**
     * Removes a {@link Collection} {@link JangineECS_Component} from the entity.
     * If the components do not belong to the entity,
     * nothing happens.
     *
     * @param components components to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvComponents(Collection<JangineECS_Component> components) {
        components.forEach(this::rmvComponent);
    }
    /**
     * Removes a {@link JangineECS_Component} from the entity.
     * If the component does not belong to the entity,
     * nothing happens.
     *
     * @param component component to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvComponent(JangineECS_Component component) {
        if (!_components.contains(component)) {
            return;
        }

        component.kill(this);

        JangineECS_System.get().rmvComponent(component);
    }


    // -+- LIFE CYCLE -+- //

    /**
     * Called upon the deletion of this entity.
     * Clears the components and deletes them properly,
     * also from {@link JangineECS_System}.
     *
     * @author Tim Kloepper
     */
    public void kill() {
        JangineECS_System.get().rmvComponents(_components);

        for (JangineECS_Component component : _components) {
            component.kill(this);
        }

        _components.clear();
    }


    // -+- GETTERS -+- //

    /**
     * Returns all {@link JangineECS_Component}, that this entity owns.
     *
     * @return all components
     *
     * @author Tim Kloepper
     */
    public HashSet<JangineECS_Component> getComponents() {
        return _components;
    }


    // -+- CHECKERS -+- //

    /**
     * Returns true, if the entity contains the specified id.
     * False, if the entity does not contain the specified id.
     *
     * @param id id to be checked
     *
     * @return whether the entity contains the id or not
     *
     * @author Tim Kloepper
     */
    public boolean hasID(int id) {
        return _ids.contains(id);
    }

    /**
     * Checks, if the entity has a {@link JangineECS_Component} of the specified class.
     *
     * @param type class of the component
     *
     * @return whether the entity has a component of that class or not
     *
     * @author Tim Kloepper
     */
    public boolean hasComponentOfType(Class<? extends JangineECS_Component> type) {
        for (JangineECS_Component component : _components) {
            if (type.isInstance(component)) {
                return true;
            }
        }

        return false;
    }
    /**
     * Checks if the entity has the specified {@link JangineECS_Component}.
     *
     * @param component component to be checked
     *
     * @return whether the entity has the specified component or not
     *
     * @author Tim Kloepper
     */
    public boolean hasComponent(JangineECS_Component component) {
        return _components.contains(component);
    }


}