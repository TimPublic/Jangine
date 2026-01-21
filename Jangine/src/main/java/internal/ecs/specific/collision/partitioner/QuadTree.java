package internal.ecs.specific.collision.partitioner;


import internal.ecs.specific.collision.CollisionComponent;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.size.SizeComponent;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static internal.ecs.specific.collision.partitioner.QuadTree.POOL;
import static internal.ecs.specific.collision.partitioner.QuadTree.SPLIT_THRESHOLD;


@SuppressWarnings("ClassEscapesDefinedScope")
public class QuadTree implements I_Partitioner {


    public static final int SPLIT_THRESHOLD = 4;
    @SuppressWarnings("ClassEscapesDefinedScope")
    public static final NodePool POOL = new NodePool();


    private Node _root;
    private HashMap<CollisionComponent, Object> _objects;

    private HashSet<Node> _leafs;


    @Override
    public void init(Vector2d position, double width, double height) {
        _root = POOL.request(new HashSet<>(), position, width, height, this::addLeaf, this::rmvLeaf);
        _objects = new HashMap<>();

        _leafs = new HashSet<>();
    }


    @Override
    public void update() {
        _root.update(_objects.values());
    }


    @Override
    public void addComponent(CollisionComponent component, PositionComponent position, SizeComponent size) {
        _objects.put(component, new Object(component, position, size));
    }
    @Override
    public void rmvComponent(CollisionComponent component) {
        _objects.remove(component);
    }


    @Override
    public Collection<CollisionComponent> getPossibleCollisions(CollisionComponent component) {
        HashSet<CollisionComponent> result;

        result = new HashSet<>();

        for (Node leaf : _leafs) {
            if (!leaf.containsRef(_objects.get(component))) {continue;}

            for (Object object : leaf.getObjects()) {
                result.add(object.collisionComponent);
            }
        }

        result.remove(component);

        return result;
    }


    public void addLeaf(Node node) {
        _leafs.add(node);
    }
    public void rmvLeaf(Node node) {
        _leafs.remove(node);
    }



}


class Node {


    private Consumer<Node> _onLeafAdded;
    private Consumer<Node> _onLeafRemoved;


    private HashSet<Node> _children;
    private HashSet<Object> _objects;
    private HashSet<Object> _rmvObjects;
    private HashSet<Object> _addObjects;

    private Vector2d _pos;
    private double _width;
    private double _height;


    public void init(HashSet<Object> objects, Vector2d position, double width, double height, Consumer<Node> leafAddedCallback, Consumer<Node> leafRemovedCallback) {
        _onLeafAdded = leafAddedCallback;
        _onLeafRemoved = leafRemovedCallback;

        _children = new HashSet<>();
        _objects = new HashSet<>();
        _rmvObjects = new HashSet<>();
        _addObjects = new HashSet<>();

        _pos = position;
        _width = width;
        _height = height;

        _addObjects(objects);

        _tryParent();
    }



    public boolean isLeaf() {
        return _children.isEmpty();
    }

    public boolean containsRef(Object obj) {
        return _objects.contains(obj);
    }

    private boolean _containsPos(Object obj) {
        double objX, objXP, objY, objYP;
        boolean xOverlap, yOverlap;

        objX = obj.pos.position.x;
        objXP = obj.pos.position.x + obj.size.width;
        objY = obj.pos.position.y;
        objYP = obj.pos.position.y + obj.size.height;

        xOverlap = objX < _pos.x + _width && objXP > _pos.x;
        yOverlap = objY < _pos.y + _height && objYP > _pos.y;

        return xOverlap && yOverlap;
    }


    public void update(Collection<Object> objects) {
        for (Object obj : objects) {
            if (_containsPos(obj)) {
                addObject(obj);

                continue;
            }

            rmvObject(obj);
        }

        _applyChanges();
    }


    public void addObject(Object obj) {
        if (!_containsPos(obj)) {
            return;
        }

        if (isLeaf()) {
            _addObjects.add(obj);

            return;
        }

        for (Node child : _children) {
            child.addObject(obj);
        }
    }
    public void rmvObject(Object obj) {
        if (isLeaf()) {
            _rmvObjects.add(obj);

            return;
        }

        for (Node child : _children) {
            child.rmvObject(obj);
        }
    }

    private void _applyChanges() {
        _objects.removeAll(_rmvObjects);
        _objects.addAll(_addObjects);

        _rmvObjects.clear();
        _addObjects.clear();

        _tryParent();
        _tryLeaf();

        for (Node child : _children) {
            child._applyChanges();
        }
    }

    private void _addObjects(Collection<Object> objects) {
        objects.forEach(this::addObject);
    }


    private void _tryParent() {
        if (_objects.size() < SPLIT_THRESHOLD) {return;}

        double hWidth, hHeight;
        Node first, second, third, fourth;

        hWidth = _width / 2;
        hHeight = _height / 2;

        first = POOL.request(_objects, new Vector2d(_pos), hWidth, hHeight, _onLeafAdded, _onLeafRemoved); // TOP LEFT
        second = POOL.request(_objects, new Vector2d(_pos).add(0, hHeight), hWidth, hHeight, _onLeafAdded, _onLeafRemoved); // BOTTOM LEFT
        third = POOL.request(_objects, new Vector2d(_pos).add(hWidth, hHeight), hWidth, hHeight, _onLeafAdded, _onLeafRemoved); // BOTTOM RIGHT
        fourth = POOL.request(_objects, new Vector2d(_pos).add(hWidth, 0), hWidth, hHeight, _onLeafAdded, _onLeafRemoved); // TOP RIGHT

        _onLeafAdded.accept(first);
        _onLeafAdded.accept(second);
        _onLeafAdded.accept(third);
        _onLeafAdded.accept(fourth);

        _children.addAll(List.of(first, second, third, fourth));
        _objects.clear();
    }
    private void _tryLeaf() {
        if (isLeaf()) {return;}

        HashSet<Object> childrenObjects;

        childrenObjects = new HashSet<>();

        for (Node child : _children) {
            childrenObjects.addAll(child._objects);
        }

        if (childrenObjects.size() >= SPLIT_THRESHOLD) {return;}

        _children.forEach(POOL::giveBack);
        _children.forEach(_onLeafRemoved);

        _children.clear();
        _objects = childrenObjects;
    }


    public Collection<Object> getObjects() {
        return _objects;
    }


}


class Object {


    public Object(CollisionComponent collisionComponent, PositionComponent position, SizeComponent size) {
        this.collisionComponent = collisionComponent;
        pos = position;
        this.size = size;
    }


    public CollisionComponent collisionComponent;
    public PositionComponent pos;
    public SizeComponent size;


}


class NodePool {


    private final HashSet<Node> _pool;


    public NodePool() {
        _pool = new HashSet<>();
    }


    public void giveBack(Node node) {
        _pool.add(node);
    }
    public Node request(HashSet<Object> objects, Vector2d position, double width, double height, Consumer<Node> leafAddedCallback, Consumer<Node> leafRemovedCallback) {
        Node node;

        if (_pool.isEmpty()) {
            node = new Node();
        } else {
            node = _pool.stream().findFirst().get();
            _pool.remove(node);
        }

        node.init(objects, position, width, height, leafAddedCallback, leafRemovedCallback);

        return node;
    }


}