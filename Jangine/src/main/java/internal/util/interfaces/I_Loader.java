package internal.util.interfaces;


public interface I_Loader<T extends Object> {


    // -+- LOADING -+- //

    T load(String path);

    // -+- ADDITION -+- //

    boolean add(String path);
    boolean add(T object);

    // -+- REMOVAL -+- //

    boolean rmv(String path);
    boolean rmv(T object);


}