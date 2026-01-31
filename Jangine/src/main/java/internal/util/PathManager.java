package internal.util;


import java.util.HashMap;


public class PathManager {


    // -+- CREATION -+- //

    public PathManager() {
        _PATHS = new HashMap<>();

        _leading = "";
        _trailing = "";
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<String, String> _PATHS;

    // NON-FINALS //

    private String _leading;
    private String _trailing;


    // -+- PATH MANAGEMENT -+- //

    public void addPath(String shortcut, String path) {
        if (_PATHS.containsKey(shortcut)) {
            throw new IllegalArgumentException("[PATH MANAGER ERROR] : Shortcut is already in use!\n"
            + "|-> Shortcut : " + shortcut);
        }

        _PATHS.put(shortcut, path);
    }
    public boolean rmvPath(String shortcut) {
        String path;

        path = _PATHS.remove(shortcut);

        return (path != null);
    }


    // -+- ADDITIONS MANAGEMENT -+- //

    public void setLeadingPath(String leading) {
        _leading = leading;
    }
    public void setTrailingPath(String trailing) {
        _trailing = trailing;
    }


    // -+- GETTERS -+- //

    public String getFullPath(String shortcut) {
        String path;

        path = _PATHS.get(shortcut);
        if (path == null) return null;

        return _leading + path + _trailing;
    }


}