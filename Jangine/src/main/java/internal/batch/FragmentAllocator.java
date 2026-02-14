package internal.batch;


import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;


public class FragmentAllocator {


    // -+- CREATION -+- //

    public FragmentAllocator(int initialSize) {
        _POSITION_TREE = new TreeMap<>();

        _root = new FragmentNode(0, initialSize);
    }

    public void clear() {
        _root = null;
        _POSITION_TREE.clear();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final TreeMap<Integer, FragmentNode> _POSITION_TREE;

    // NON-FINALS //

    private FragmentNode _root;


    // -+- FRAGMENT MANAGEMENT -+- //

    public void addFragment(FragmentNode fragment) {
        if (fragment == null) throw new IllegalArgumentException("[FRAGMENT ALLOCATOR ERROR] : Fragment cannot be null!");
        if (fragment.size <= 1) return;
        if (fragment.position < 0) return;

        _POSITION_TREE.put(fragment.position, fragment);

        if (_root == null) _root = fragment;
        else _root.addNode(fragment);

        h_tryMerging();
    }
    public void rmvFragment(FragmentNode fragment) {
        if (fragment == null) throw new IllegalArgumentException("[FRAGMENT ALLOCATOR ERROR] : Fragment cannot be null!");
        if (_root == null) return;

        _POSITION_TREE.remove(fragment.position);

        if (_root == fragment) {
            HashSet<FragmentNode> rootChildren;

            rootChildren = _root.getChildren();
            _root.clearChildren();

            _root = null;

            rootChildren.forEach(this::addFragment);

            return;
        }

        _root.rmvNode(fragment);
    }

    private void h_tryMerging() {
        for (FragmentNode node : _POSITION_TREE.values()) {
            FragmentNode lower, higher;
            Map.Entry<Integer, FragmentNode> entry;

            lower = null;
            higher = null;

            entry = _POSITION_TREE.lowerEntry(node.position);
            if (entry != null) lower = entry.getValue();
            entry = _POSITION_TREE.higherEntry(node.position);
            if (entry != null) higher = entry.getValue();

            if (lower != null) {
                if (lower.isConnectedWith(node)) {
                    rmvFragment(lower);
                    rmvFragment(node);

                    node.merge(lower);

                    addFragment(node);

                    return;
                }
            }
            if (higher != null) {
                if (higher.isConnectedWith(node)) {
                    rmvFragment(higher);
                    rmvFragment(node);

                    node.merge(higher);

                    addFragment(node);

                    return;
                }
            }
        }
    }


    // -+- MEMORY MANAGEMENT -+- //

    public int allocate(int amount) {
        FragmentNode currentNode;

        currentNode = _root;

        while (currentNode != null) {
            if (currentNode.canAllocate(amount)) {
                if (currentNode.lower != null) {
                    if (currentNode.lower.canAllocate(amount)) {
                        currentNode = currentNode.lower;
                    }
                }

                rmvFragment(currentNode);

                int result;

                result = currentNode.allocate(amount);

                addFragment(currentNode);

                return result;
            }

            currentNode = currentNode.higher;
        }

        return -1;
    }


}