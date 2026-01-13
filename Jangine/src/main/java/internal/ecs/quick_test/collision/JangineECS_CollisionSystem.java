package internal.ecs.quick_test.collision;


import internal.ecs.collision.help.I_SpatialPartitioning;
import internal.ecs.quick_test.*;

import java.util.HashSet;
import java.util.function.Consumer;


public class JangineECS_CollisionSystem extends JangineECS_ComponentSystem {


    private final I_SpatialPartitioning _partitioner;

    private final HashSet<Consumer<JangineEntityCollisionData>> _entityCollisionCallbacks;
    private final HashSet<Consumer<JangineWindowCollisionData>> _windowCollisionCallbacks;


    public JangineECS_CollisionSystem(I_SpatialPartitioning partitioner) {
        _partitioner = partitioner;

        _entityCollisionCallbacks = new HashSet<>();
        _windowCollisionCallbacks = new HashSet<>();
    }


    // -+- UPDATE LOOP -+- //

    @Override
    public void update() {
        _partitioner.update();
    }


    // -+- COMPONENT -+- //

    @Override
    protected void _onAddComponent(JangineECS_Component component) {
        _partitioner.addComponent(component);
    }
    @Override
    protected void _onRmvComponent(JangineECS_Component component) {
        _partitioner.rmvComponent(component);
    }


    // -+- CALLBACKS -+- //

    public void addEntityCollisionCallback(Consumer<JangineEntityCollisionData> callback) {
        _entityCollisionCallbacks.add(callback);
    }
    public void rmvEntityCollisionCallback(Consumer<JangineEntityCollisionData> callback) {
        _entityCollisionCallbacks.remove(callback);
    }

    public void addWindowCollisionCallback(Consumer<JangineWindowCollisionData> callback) {
        _windowCollisionCallbacks.add(callback);
    }
    public void rmvWindowCollisionCallback(Consumer<JangineWindowCollisionData> callback) {
        _windowCollisionCallbacks.remove(callback);
    }

    private void _sendEntityCollisionData(JangineEntityCollisionData data) {
        for (Consumer<JangineEntityCollisionData> callback : _entityCollisionCallbacks) {
            callback.accept(data);
        }
    }
    private void _sendWindowCollisionData(JangineWindowCollisionData data) {
        for (Consumer<JangineWindowCollisionData> callback : _windowCollisionCallbacks) {
            callback.accept(data);
        }
    }


}