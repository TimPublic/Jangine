package internal.resource;


public abstract class A_Resource {


    // -+- CREATION -+- //

    public A_Resource(String path) {
        _PATH = path;

        _disposed = false;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final String _PATH;

    // NON-FINALS //

    private boolean _disposed;


    // -+- DATA MANAGEMENT -+- //

    public void dispose() {
        _disposed = true;

        p_dispose();
    }
    protected abstract void p_dispose();


    // -+- GETTERS -+- //

    public String getPath() {
        return _PATH;
    }


}