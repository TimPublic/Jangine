package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_JangineTextureLoader;

import java.util.HashMap;


// An atlas-texture is a child of the texture class but contains a regions hash-map.
// This hash-map maps string to an array of floats, that represent the four
// uv-coordinates.
// This array is guaranteed to have a length of exactly four.
public class JangineAtlasTexture extends JangineTexture {


    private HashMap<String, Float[]> _regions;


    public JangineAtlasTexture(String filePath, I_JangineTextureLoader textureLoader) {
        super(filePath, textureLoader);

        _regions = new HashMap<>();
    }


    // -+- REGION-MANAGEMENT -+- //

    // Creates a region with the specified name and the specified coordinates.
    // The array of coordinates needs to be exactly four otherwise, the engine crashes.
    // If another region of this name exists, it will be overwritten.
    public void createRegion(String regionName, Float[] coordinates) {
        if (coordinates.length != 4) {
            System.err.println("[ATLAS-TEXTURE ERROR] : Not exactly four coordinates!");
            System.err.println("|-> Amount : " + coordinates.length);

            System.exit(1);
        }

        _regions.put(regionName, coordinates);
    }
    // Removes a region by name, if this region does not exist,
    // the engine crashes.
    public void removeRegion(String regionName) {
        if (!_regions.containsKey(regionName)) {
            System.err.println("[ATLAS-TEXTURE ERROR] : Region with specified name does not exist!");
            System.err.println("|-> Name : " + regionName);

            System.exit(1);
        }

        _regions.remove(regionName);
    }
    // Checks if a region, specified by name, exists.
    // Returns a respective boolean.
    public boolean doesRegionExist(String regionName) {
        return _regions.containsKey(regionName);
    }


    // -+- GETTERS -+- //

    // Returns the coordinates of a specific region, specified by name.
    // If this region does not exist, the engine will crash.
    // The coordinates will be in the form of an array of float,
    // with the guaranteed size of exactly four floats.
    public Float[] getRegionCoordinates(String regionName) {
        if (!_regions.containsKey(regionName)) {
            System.err.println("[ATLAS-TEXTURE ERROR] : Region with specified name does not exist!");
            System.err.println("|-> Name : " + regionName);

            System.exit(1);
        }

        return _regions.get(regionName);
    }


}