package internal.resource;


import java.util.HashMap;
import java.util.function.Function;


public class ResourceManager<T extends A_Resource> {


    // -+- CREATION -+- //

    public ResourceManager(Function<String, T> factory) {
        _RES = new HashMap<>();
        _FACTORY = factory;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<String, T> _RES;
    private final Function<String, T> _FACTORY;


    // -+- RESOURCE MANAGEMENT -+- //

    // LOADING //

    public T load(String path) {
        T resource;

        resource = _RES.get(path);
        if (resource == null) _FACTORY.apply(path);

        return resource;
    }

    // ADDING //

    public boolean add(String path) {
        return load(path) != null;
    }
    public boolean add(T resource, boolean overwrite) {
        String path;
        T foundResource;

        path = resource.getPath();
        foundResource = _RES.get(path);

        if (foundResource == null) {
            _RES.put(path, resource);

            return true;
        }
        if (foundResource == resource) return true;

        if (!overwrite) return false;

        foundResource.dispose();
        _RES.put(path, resource);

        return true;
    }

    public boolean rmv(String path) {
        T foundResource;

        foundResource = _RES.get(path);

        if (foundResource == null) return false;

        foundResource.dispose();

        return true;
    }
    public boolean rmv(T resource) {
        String path;
        T foundResource;

        path = resource.getPath();
        foundResource = _RES.get(path);

        if (foundResource == null) return true;
        if (foundResource != resource) return false;

        resource.dispose();

        return true;
    }


}