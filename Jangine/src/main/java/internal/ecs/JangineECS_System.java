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


    // -+- CREATION -+- //

    public JangineECS_System() {
        _entities = new HashMap<>();
        _freedEntityIDs = new ArrayList<>();

        _componentSystems = new HashMap<>();
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


    // -+- COMPONENT MANAGEMENT -+- //

    public void addComponent(int entityID, JangineECS_Component component, boolean overwrite) {
        if (!_entities.containsKey(entityID)) {
            return;
        }

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

            _componentSystems.get(componentClass).rmvComponent(component);
        }

        _entities.get(entityID).put(componentClass, component);
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

        _entities.get(entityID).remove(component);
        _componentSystems.get(componentClass).rmvComponent(component);
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

        _entities.get(entityID).remove(component);
        _componentSystems.get(componentClass).rmvComponent(component);
    }


    // -+- COMPONENT SYSTEM MANAGEMENT -+- //

    public void addComponentSystem(Class<? extends JangineECS_Component> componentClass, JangineECS_ComponentSystem componentSystem, boolean overwrite) {
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


}