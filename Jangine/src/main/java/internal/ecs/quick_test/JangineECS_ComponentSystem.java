package internal.ecs.quick_test;


import java.util.HashMap;
import java.util.HashSet;


public abstract class JangineECS_ComponentSystem {


    protected final HashMap<Integer, JangineECS_Component> _components;


    public JangineECS_ComponentSystem() {
        _components = new HashMap<>();
    }


    // -+- LIFE CYCLE -+- //

    public void init(JangineECS system) {}
    public void kill(JangineECS system) {}


    // -+- UPDATE LOOP -+- //

    public void update() {}
    public final void updateComponents() {
        for (JangineECS_Component component : _components.values()) {
            if (component.active) {
                _onUpdateComponent(component);
            }
        }
    }

    protected void _onUpdateComponent(JangineECS_Component component) {}


    // -+- COMPONENT -+- //

    public final void addComponent(int entityID, JangineECS_Component component, boolean overwrite) {
        boolean alreadyHasComponentForEntity;

        alreadyHasComponentForEntity = _components.containsKey(entityID);

        if (alreadyHasComponentForEntity && !overwrite) {return;}
        if (alreadyHasComponentForEntity) {
            // Kill old component properly.
        }

        component.init(entityID);

        _components.put(entityID, component);

        _onAddComponent(component);
    }
    public final void rmvComponent(int entityID, JangineECS_Component component) {
        if (!_components.containsKey(entityID)) {return;}
        if (_components.get(entityID) != component) {return;}

        component.kill(entityID);

        _components.remove(entityID);

        _onRmvComponent(component);
    }

    protected void _onAddComponent(JangineECS_Component component) {}
    protected void _onRmvComponent(JangineECS_Component component) {}


}