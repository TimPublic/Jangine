package internal.entity_component_system.builders;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_System;


public class EntityBuilder {


    // -+- CREATION -+- //

    public EntityBuilder() {
        _currentEntityId = -1;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private int _currentEntityId;
    private A_System _currentSystem;


    // -+- START AND STOP -+- //

    /**
     * Starts the initialization of an entity in the
     * specified entity.
     * This method will create a new entity.
     *
     * @param system The system the entity should be created in
     *
     * @return This builder for linked building
     *
     * @author Tim Kloepper
     */
    public EntityBuilder start(A_System system) {
        if (_currentEntityId != -1) {
            throw new IllegalStateException("Already building an entity!");
        }

        _currentSystem = system;
        _currentEntityId = _currentSystem.addEntity();

        return this;
    }
    /**
     * Sets the builder to the specified system and entity,
     * in order to add or remove components from the entity
     * in a linked way.
     *
     * @param system The system the entity is in
     * @param entityID The entity that should be operated upon
     *
     * @return The builder for linked building
     *
     * @author Tim Kloepper
     */
    public EntityBuilder stepIn(A_System system, int entityID) {
        if (_currentEntityId != -1) {
            throw new IllegalStateException("Already building an entity!");
        }

        _currentSystem = system;
        _currentEntityId = entityID;

        return this;
    }
    /**
     * Finishes the initialization and returns the id of the created entity.
     * After calling this method, the builder is ready for a new building
     * process with {@link EntityBuilder#start(A_System)} or {@link EntityBuilder#stepIn(A_System, int)}.
     *
     * @return The id of the created entity
     *
     * @author Tim Kloepper
     */
    public int finish() {
        int id;

        if (_currentEntityId == -1) {
            throw new IllegalStateException("No entity was initialized!");
        }

        id = _currentEntityId;

        _currentEntityId = -1;
        _currentSystem = null;

        return id;
    }
    /**
     * Stops the initialization and removes the created entity.
     */
    public void stop() {
        if (_currentEntityId != -1) {
            _currentSystem.rmvEntity(_currentEntityId);
        }

        _currentEntityId = -1;
        _currentSystem = null;
    }


    // -+- ADDITION AND REMOVAL -+- //

    /**
     * Adds a component to the currently set entity.
     * If no creation is currently running, an {@link IllegalStateException}
     * is thrown.
     *
     * @param component The component that is to be added
     *
     * @return The builder for linked creation
     *
     * @author Tim Kloepper
     */
    public EntityBuilder add(A_Component component) {
        if (_currentEntityId == -1) {
            throw new IllegalStateException("No entity was initialized!");
        }

        _currentSystem.addComponentToEntity(_currentEntityId, component);

        return this;
    }
    /**
     * Removes a component from the currently set entity.
     * If no creation is currently running, an {@link IllegalStateException}
     * is thrown.
     *
     * @param component The component that is to be removed
     *
     * @return The builder for linked creation
     *
     * @author Tim Kloepper
     */
    public EntityBuilder rmv(A_Component component) {
        if (_currentEntityId == -1) {
            throw new IllegalStateException("No entity was initialized!");
        }

        _currentSystem.rmvComponentFromEntity(_currentEntityId, component.getClass());

        return this;
    }


    // -+- GETTERS -+- //

    /**
     * Retrieves the entity that is currently being build.
     * If none is being build {@code -1} is returned.
     *
     * @return The id of the currently build entity
     *
     * @author Tim Kloepper
     */
    public int get() {
        if (_currentEntityId == -1) {
            throw new IllegalStateException("No entity was initialized!");
        }

        return _currentEntityId;
    }


    // -+- CHECKERS -+- //

    /**
     * Checks if the builder is currently building.
     * This is checked by retrieving the current value
     * of the set entity.
     *
     * @return Whether the builder is building or not
     *
     * @author Tim Kloepper
     */
    public boolean isBuilding() {
        return (_currentEntityId != -1);
    }


}