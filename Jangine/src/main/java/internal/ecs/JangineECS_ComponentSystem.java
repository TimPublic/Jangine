package internal.ecs;


import java.util.HashSet;


public abstract class JangineECS_ComponentSystem {


    private HashSet<JangineECS_Component> _components;


    public JangineECS_ComponentSystem() {
        _components = new HashSet<>();
    }


    public abstract void update(double deltaTime);


    public void rmvComponent(JangineECS_Component component) {
        _components.add(component);
    }
    public void addComponent(JangineECS_Component component) {
        _components.remove(component);
    }


}