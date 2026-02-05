package internal.util;


import java.util.HashMap;


public class PathManager {


    // -+- CREATION -+- //

    public PathManager() {
        _PATHS = new HashMap<>();

        _lead = "";
        _trail = "";
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<String, String> _PATHS;

    // NON-FINALS //

    private String _lead;
    private String _trail;


    // -+- PATH MANAGEMENT -+- //

    public void add(String shortcut, String path) {
        if (_PATHS.containsKey(shortcut)) {
            throw new IllegalArgumentException("[PATH MANAGER ERROR] : Shortcut is already in use!\n"
            + "|-> Shortcut : " + shortcut);
        }

        _PATHS.put(shortcut, path);
    }
    public boolean rmv(String shortcut) {
        String path;

        path = _PATHS.remove(shortcut);

        return (path != null);
    }


    // -+- ADDITIONS MANAGEMENT -+- //

    public void setLead(String leading) {
        _lead = leading;
    }
    public void setTrail(String trailing) {
        _trail = trailing;
    }


    // -+- GETTERS -+- //

    public String get(String shortcut) {
        String path;

        path = _PATHS.get(shortcut);
        if (path == null) return null;

        return _lead + path + _trail;
    }
    public String getOriginal(String shortcut) {
        return _PATHS.get(shortcut);
    }

    public String getLead() {
        return _lead;
    }
    public String getTrail() {
        return _trail;
    }


}