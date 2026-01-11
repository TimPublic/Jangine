package internal.ecs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class JangineECS_System {


    private static JangineECS_System _instance;


    private final HashMap<Integer, HashMap<Class<? extends JangineECS_Component>, JangineECS_Component>> _entities;
    private final ArrayList<Integer> _freedEntityIDs;
    private int _nextEntityID;

    private final HashMap<Class<? extends JangineECS_Component>, JangineECS_ComponentSystem> _componentSystems;


    // -+- CREATION -+- //

    private JangineECS_System() {
        _entities = new HashMap<>();
        _freedEntityIDs = new ArrayList<>();

        _componentSystems = new HashMap<>();
    }

    public static JangineECS_System get() {
        if (_instance == null) {
            _instance = new JangineECS_System();
        }

        return _instance;
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

        _entities.remove(entityID);
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

        _entities.remove(entityID);
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