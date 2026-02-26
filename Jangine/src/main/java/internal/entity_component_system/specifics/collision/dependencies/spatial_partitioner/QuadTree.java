package internal.entity_component_system.specifics.collision.dependencies.spatial_partitioner;


import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.entity_component_system.specifics.hitbox.CircleHitboxComponent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.rendering.container.A_Container;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;


public class QuadTree implements I_SpatialPartitioner {


    // -+- CREATION -+- //

    public QuadTree(Vector2d position, double width, double height) {
        _OBJECTS = new HashSet<>();
        _LEAFS = new HashSet<>();

        _ROOT = new Node(this::onLeafCreated, this::onLeafRemoved, new NodePool(this::onLeafCreated, this::onLeafRemoved));
        _ROOT.init(List.of(), position, width, height);
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public static final int SPLITTING_THRESHOLD = 4;

    private final HashSet<ObjectData> _OBJECTS;
    private final HashSet<Node> _LEAFS;

    private final Node _ROOT;


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(A_Container container) {
        _ROOT.update(_OBJECTS);
    }


    // -+- ADDITION AND REMOVAL -+- //

    @Override
    public void addObject(ObjectData obj) {
        _OBJECTS.add(obj);
    }
    @Override
    public void rmvObject(ObjectData obj) {
        _OBJECTS.remove(obj);
    }


    // -+- CALLBACKS -+- //

    public void onLeafCreated(@SuppressWarnings("ClassEscapesDefinedScope") Node leaf) {
        _LEAFS.add(leaf);
    }
    public void onLeafRemoved(@SuppressWarnings("ClassEscapesDefinedScope") Node leaf) {
        _LEAFS.remove(leaf);
    }


    // -+- GETTERS -+- //

    @Override
    public Collection<ObjectData> getCollidingObjects(ObjectData obj) {
        HashSet<ObjectData> result;

        result = new HashSet<>();

        for (Node leaf : _LEAFS) {
            if (!leaf.containsRef(obj.hitboxComponent.owningEntity)) continue;

            result.addAll(leaf.p_OBJECTS);
        }

        ObjectData objRemoved;

        objRemoved = null;

        for (ObjectData objectData : result) {
            if (objectData.hitboxComponent.owningEntity == obj.hitboxComponent.owningEntity) {
                objRemoved = objectData;
            }
        }

        result.remove(objRemoved);

        return result;
    }


}


class Node {


    // -+- CREATION -+- //

    public Node(Consumer<Node> leafCreatedCallback, Consumer<Node> leafRemovedCallback, NodePool pool) {
        p_OBJECTS = new HashSet<>();
        _ADD_OBJECTS = new HashSet<>();
        _RMV_OBJECTS = new HashSet<>();
        _NODES = new Node[4];

        _LEAF_CREATED_CALLBACK = leafCreatedCallback;
        _LEAF_REMOVED_CALLBACK = leafRemovedCallback;

        _LEAF_CREATED_CALLBACK.accept(this);

        _POOL = pool;

        _isLeaf = true;
    }

    public void init(Collection<ObjectData> objects, Vector2d position, double width, double height) {
        _position = position;
        _width = width;
        _height = height;

        for (ObjectData object : objects) {
            if (!containsPos(object)) continue;

            p_OBJECTS.add(object);
        }
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final Consumer<Node> _LEAF_CREATED_CALLBACK;
    private final Consumer<Node> _LEAF_REMOVED_CALLBACK;

    protected final HashSet<ObjectData> p_OBJECTS;
    private final HashSet<ObjectData> _ADD_OBJECTS;
    private final HashSet<ObjectData> _RMV_OBJECTS;
    private final Node[] _NODES;

    private final NodePool _POOL;

    // NON-FINALS //

    private boolean _isLeaf;

    private Vector2d _position;
    private double _width, _height;


    // -+- UPDATE LOOP -+- //

    public void update(Collection<ObjectData> objects) {
        for (ObjectData obj : objects) {
            p_updateObject(obj);
        }

        p_applyChanges();
    }

    protected void p_updateObject(ObjectData obj) {
        boolean pos;

        pos = containsPos(obj);

        if (!pos) {
            rmvObject(obj);

            return;
        }

        if (_isLeaf && !containsRef(obj.hitboxComponent.owningEntity)) {
            addObject(obj);

            return;
        }

        if (_NODES[0] == null) return;
        for (Node node : _NODES) {
            node.p_updateObject(obj);
        }
    }
    protected void p_applyChanges() {
        p_OBJECTS.addAll(_ADD_OBJECTS);
        p_OBJECTS.removeAll(_RMV_OBJECTS);

        if (_isLeaf) _tryToCreateChildren();
        else _tryToRemoveChildren();

        if (_NODES[0] == null) return;
        for (Node child : _NODES) child.p_applyChanges();
    }


    // -+- CHILD MANAGEMENT -+- //

    private void _tryToCreateChildren() {
        if (p_OBJECTS.size() < QuadTree.SPLITTING_THRESHOLD) return;

        double halvedWidth, halvedHeight;

        halvedWidth = _width / 2;
        halvedHeight = _height / 2;

        _NODES[0] = _POOL.request(p_OBJECTS, new Vector2d(_position), halvedWidth, halvedHeight);
        _NODES[1] = _POOL.request(p_OBJECTS, new Vector2d(_position.x, _position.y + halvedHeight), halvedWidth, halvedHeight);
        _NODES[2] = _POOL.request(p_OBJECTS, new Vector2d(_position.x + halvedWidth, _position.y), halvedWidth, halvedHeight);
        _NODES[3] = _POOL.request(p_OBJECTS, new Vector2d(_position.x + halvedWidth, _position.y + halvedHeight), halvedWidth, halvedHeight);

        _isLeaf = false;
    }
    private void _tryToRemoveChildren() {
        if (_isLeaf) return;
        if (!_NODES[0]._isLeaf) return;

        for (Node child : _NODES) {
            p_OBJECTS.addAll(child.p_OBJECTS);
        }

        if (p_OBJECTS.size() >= QuadTree.SPLITTING_THRESHOLD) {
            p_OBJECTS.clear();

            return;
        }

        for (int index = 0; index < _NODES.length; index++) {
            _POOL.giveBack(_NODES[index]);
            _LEAF_REMOVED_CALLBACK.accept(_NODES[index]);
            _NODES[index] = null;
        }

        _isLeaf = true;
    }


    // -+- OBJECT MANAGEMENT -+- //

    public void addObject(ObjectData obj) {
        if (!isLeaf()) {
            for (Node node : _NODES) {
                if (node == null) continue;
                node.addObject(obj);
            }

            _ADD_OBJECTS.add(obj);
        }

        // Avoid unnecessary position check.
        if (containsRef(obj.hitboxComponent.owningEntity)) return;
        if (!containsPos(obj)) return;

        _ADD_OBJECTS.add(obj);
    }
    public void rmvObject(ObjectData obj) {
        if (!isLeaf()) {
            for (Node node : _NODES) {
                if (node == null) continue;
                node.rmvObject(obj);
            }

            return;
        }

        _RMV_OBJECTS.add(obj);
    }


    // -+- CHECKERS -+- //

    public boolean isLeaf() {
        return _isLeaf;
    }

    public boolean containsPos(ObjectData obj) {
        boolean xOverlap, yOverlap;

        xOverlap = false;
        yOverlap = false;

        if (obj.hitboxComponent instanceof RectangleHitboxComponent) {
            xOverlap = obj.positionComponent.position.x < _position.x + _width && obj.positionComponent.position.x + ((RectangleHitboxComponent) obj.hitboxComponent).width > _position.x;
            yOverlap = obj.positionComponent.position.y < _position.y + _height && obj.positionComponent.position.y + ((RectangleHitboxComponent) obj.hitboxComponent).height > _position.y;
        } else if (obj.hitboxComponent instanceof CircleHitboxComponent) {
            xOverlap = obj.positionComponent.position.x - ((CircleHitboxComponent) obj.hitboxComponent).radius < _position.x + _width && obj.positionComponent.position.x + ((CircleHitboxComponent) obj.hitboxComponent).radius > _position.x;
            yOverlap = obj.positionComponent.position.y - ((CircleHitboxComponent) obj.hitboxComponent).radius < _position.y + _height && obj.positionComponent.position.y + ((CircleHitboxComponent) obj.hitboxComponent).radius > _position.y;
        }

        return xOverlap && yOverlap;
    }
    public boolean containsRef(int entity) {
        for (ObjectData objectData : p_OBJECTS) {
            if (objectData.hitboxComponent.owningEntity == entity) return true;
        }

        return false;
    }


}

class NodePool {


    // -+- CREATION -+- //

    public NodePool(Consumer<Node> leafCreatedCallback, Consumer<Node> leafRemovedCallback) {
        _NODES = new HashSet<>();

        _LEAF_CREATED_CALLBACK = leafCreatedCallback;
        _LEAF_REMOVED_CALLBACK = leafRemovedCallback;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashSet<Node> _NODES;

    private final Consumer<Node> _LEAF_CREATED_CALLBACK;
    private final Consumer<Node> _LEAF_REMOVED_CALLBACK;


    // -+- NODE MANAGEMENT -+- //

    public Node request(Collection<ObjectData> objects, Vector2d position, double width, double height) {
        Node node;

        if (_NODES.isEmpty()) {
            node = new Node(_LEAF_CREATED_CALLBACK, _LEAF_REMOVED_CALLBACK, this);
        } else {
            node = _NODES.iterator().next();
            _NODES.remove(node);
        }

        node.init(objects, position, width, height);

        return node;
    }
    public void giveBack(Node node) {
        node.p_OBJECTS.clear();

        _NODES.add(node);
    }


}