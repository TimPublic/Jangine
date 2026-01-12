package internal.ecs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class JangineECS_System {


    private final HashMap<Integer, HashMap<Class<? extends JangineECS_Component>, JangineECS_Component>> _entities;
    private final ArrayList<Integer> _freedEntityIDs;
    private int _nextEntityID;

    private final HashMap<Class<? extends JangineECS_Component>, JangineECS_ComponentSystem> _componentSystems;

    // This will have duplicate entity ids, but we do not care about that, because we have unique keys.
    private static final HashMap<JangineECS_Component, Integer> _componentToEntity = new HashMap<>();

    private final HashMap<Class<? extends JangineECS_Component>, JangineECS_Component> _bufferedCreationMap;
    private boolean _isCreatingBuffered;


    // -+- CREATION -+- //

    public JangineECS_System() {
        _entities = new HashMap<>();
        _freedEntityIDs = new ArrayList<>();

        _componentSystems = new HashMap<>();

        _bufferedCreationMap = new HashMap<>();
    }


    // -+- UPDATE LOOP -+- //

    public void update(double deltaTime) {
        for (JangineECS_ComponentSystem componentSystem : _componentSystems.values()) {
            componentSystem.update(deltaTime);
        }
    }


    // -+- ENTITY MANAGEMENT -+- //

    public int addEntity() {
        int entityID;

        if (_freedEntityIDs.isEmpty()) {
            entityID = _nextEntityID;
            _nextEntityID++;
        } else {
            entityID = _freedEntityIDs.get(0);
            _freedEntityIDs.remove(entityID);
        }

        _entities.put(entityID, new HashMap<>());

        return entityID;
    }
    public void rmvEntity(int entityID) {
        if (!_entities.containsKey(entityID)) {
            return;
        }

        _entities.remove(entityID);
        _freedEntityIDs.add(entityID);
    }


    // -+- BUFFERED ENTITY CREATION -+- //

    /**
     * Starts the process of a buffered entity creation.
     * Meaning, that you first provide the system with components
     * with {@link JangineECS_System#addComponentToCreation(JangineECS_Component, boolean)}
     * to then call {@link JangineECS_System#endEntityCreation()}, which will then
     * actually generate an entity id as well as initialize the components
     * (see {@link JangineECS_Component#init(JangineECS_System)}).
     *
     * @author Tim Kloepper
     */
    public void startEntityCreation() {
        if (_isCreatingBuffered) {
            System.err.println("[ECS ERROR] : Tried starting new buffered creation while still doing another!");

            System.exit(1);

            return;
        }

        _bufferedCreationMap.clear();

        _isCreatingBuffered = true;
    }
    /**
     * Is part of the buffered entity creation process.
     * Should be only called after {@link JangineECS_System#startEntityCreation()}
     * to add components to the buffer.
     * For the case of a component of the same class already existing in the buffer,
     * you can choose to overwrite it, or not to add the new component.
     *
     * @param component component to be added
     * @param overwrite whether a component of the same type should be overwritten if already existing
     *
     * @author Tim Kloepper
     */
    public void addComponentToCreation(JangineECS_Component component, boolean overwrite) {
        Class<? extends JangineECS_Component> componentClass;

        _checkComponentExists(component);

        componentClass = component.getClass();

        if (!_componentSystems.containsKey(componentClass)) {
            return;
        }

        if (_bufferedCreationMap.containsKey(componentClass) && !overwrite) {
            return;
        }

        _bufferedCreationMap.put(componentClass, component);
    }
    /**
     * Stops the buffered entity creation process, generates an entity id and handles further component
     * initialization (see {@link JangineECS_Component#init(JangineECS_System)}).
     *
     * @return the entity id
     *
     * @author Tim Kloepper
     */
    public int endEntityCreation() {
        int entityID;

        if (!_isCreatingBuffered) {
            System.err.println("[ECS ERROR] : Tried to end buffered creation without starting it!");

            System.exit(1);

            return -1;
        }

        _isCreatingBuffered = false;

        if (_freedEntityIDs.isEmpty()) {
            entityID = _nextEntityID;
            _nextEntityID++;
        } else {
            entityID = _freedEntityIDs.get(0);
            _freedEntityIDs.remove(entityID);
        }

        _entities.put(entityID, _bufferedCreationMap);

        for (JangineECS_Component component : _bufferedCreationMap.values()) {
            component.init(this);
        }

        return entityID;
    }
    /**
     * Breaks the entity creation process, deleting all added components and loosing all accumulated data.
     *
     * @author Tim Kloepper
     */
    public void breakEntityCreation() {
        _bufferedCreationMap.clear();

        _isCreatingBuffered = false;
    }


    // -+- COMPONENT MANAGEMENT -+- //

    public void addComponent(int entityID, JangineECS_Component component, boolean overwrite) {
        if (!_entities.containsKey(entityID)) {
            return;
        }

        _checkComponentExists(component);

        Class<? extends JangineECS_Component> componentClass;
        HashMap<Class<? extends JangineECS_Component>, JangineECS_Component> entityComponentMap;
        boolean alreadyHasComponent;

        componentClass = component.getClass();

        if (!_componentSystems.containsKey(componentClass)) {
            return;
        }

        entityComponentMap = _entities.get(entityID);
        alreadyHasComponent = entityComponentMap.containsKey(componentClass);

        if (alreadyHasComponent) {
            if (!overwrite) {
                return;
            }

            // Basically rmvComponent, but this is faster, as we already did checks and gathered information such as the class.

            JangineECS_Component currentComponent;
            currentComponent = entityComponentMap.get(componentClass);

            currentComponent.kill(this);
            //noinspection unchecked
            _componentSystems.get(componentClass).rmvComponent(currentComponent);
        }

        component.init(this);

        _entities.get(entityID).put(componentClass, component);
        //noinspection unchecked
        _componentSystems.get(componentClass).addComponent(component);
        _componentToEntity.put(component, entityID);
    }

    public void rmvComponent(int entityID, JangineECS_Component component) {
        if (!_entities.containsKey(entityID)) { // Entity does not exist
            return;
        }

        Class<? extends JangineECS_Component> componentClass;
        componentClass = component.getClass();

        if (!_componentSystems.containsKey(componentClass)) { // Component can not be managed -> Will not be there
            return;
        }
        if (!_entities.get(entityID).containsKey(componentClass)) { // Entity does not hold this component
            return;
        }

        for (JangineECS_Component entityComponent : _entities.get(entityID).values()) {
            _componentToEntity.remove(entityComponent);
        }

        component.kill(this);

        _entities.get(entityID).remove(component);
        //noinspection unchecked
        _componentSystems.get(componentClass).rmvComponent(component);
        _componentToEntity.remove(component);
    }
    public void rmvComponentByClass(int entityID, Class<? extends JangineECS_Component> componentClass) {
        if (!_entities.containsKey(entityID)) { // Entity does not exist
            return;
        }
        if (!_componentSystems.containsKey(componentClass)) { // Component can not be managed -> Will not be there
            return;
        }
        if (!_entities.get(entityID).containsKey(componentClass)) { // Entity does not hold this component
            return;
        }

        JangineECS_Component component;
        component = _entities.get(entityID).get(componentClass);

        for (JangineECS_Component entityComponent : _entities.get(entityID).values()) {
            _componentToEntity.remove(entityComponent);
        }

        component.kill(this);

        //noinspection SuspiciousMethodCalls
        _entities.get(entityID).remove(component);
        //noinspection unchecked
        _componentSystems.get(componentClass).rmvComponent(component);
        _componentToEntity.remove(component);
    }


    // -+- COMPONENT SYSTEM MANAGEMENT -+- //

    public void addComponentSystem(Class<? extends JangineECS_Component> componentClass, JangineECS_ComponentSystem<? extends JangineECS_Component> componentSystem, boolean overwrite) {
        if (_componentSystems.containsKey(componentClass) && !overwrite) {
            return;
        }

        _componentSystems.put(componentClass, componentSystem);
    }


    // -+- GETTERS -+- //

    public JangineECS_Component getComponentOfEntityByClass(int entityID, Class<? extends JangineECS_Component> componentClass) {
        if (!_entities.containsKey(entityID)) {
            return null;
        }

        return _entities.get(entityID).get(componentClass);
    }
    public HashSet<JangineECS_Component> getComponentsOfEntity(int entityID) {
        if (!_entities.containsKey(entityID)) {
            return null;
        }

        return new HashSet<>(_entities.get(entityID).values());
    }

    public static int findEntityByComponent(JangineECS_Component component) {
        if (!_componentToEntity.containsKey(component)) {
            return -1;
        }

        return _componentToEntity.get(component);
    }


    // -+- CHECKERS -+- //

    public boolean entityHasComponentByClass(int entityID, Class<? extends JangineECS_Component> componentClass) {
        if (!_entities.containsKey(entityID)) {
            return false;
        }

        return _entities.get(entityID).containsKey(componentClass);
    }
    public boolean entityHasComponent(int entityID, JangineECS_Component component) {
        if (!_entities.containsKey(entityID)) {
            return false;
        }

        return _entities.get(entityID).containsValue(component);
    }

    private void _checkComponentExists(JangineECS_Component component) {
        if (_componentToEntity.containsKey(component)) {
            System.err.println("[ECS ERROR] : Component is already assigned to an entity!");
            System.err.println("|-> Component : " + component);
            System.err.println("|-> Entity : " + _componentToEntity.get(component));

            System.exit(1);
        }
    }


}