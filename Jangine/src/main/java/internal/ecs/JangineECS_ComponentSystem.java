package internal.ecs;


import java.util.HashSet;


public abstract class JangineECS_ComponentSystem<T extends JangineECS_Component> {


    protected HashSet<T> _components;


    public JangineECS_ComponentSystem() {
        _components = new HashSet<>();
    }


    public abstract void update(double deltaTime);


    public void rmvComponent(T component) {
        _components.remove(component);
    }
    public void addComponent(T component) {
        _components.add(component);
    }


}