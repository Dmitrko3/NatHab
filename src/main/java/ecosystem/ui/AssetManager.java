package ecosystem.ui;

import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton manager that loads and caches image assets.
 */
public class AssetManager {

    private static final AssetManager INSTANCE = new AssetManager();

    private final Map<String, ImageIcon> iconCache;

    private AssetManager() {
        this.iconCache = new HashMap<>();
    }

    /**
     * Gets the single instance of the AssetManager.
     *
     * @return the singleton instance
     */
    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves an icon from the cache or loads it from the file system/classpath.
     *
     * @param path the resource path of the image
     * @return the loaded image icon
     * @throws IllegalArgumentException if the image cannot be found
     */
    public ImageIcon getIcon(String path) {
        if (!iconCache.containsKey(path)) {
            URL imageUrl = getClass().getClassLoader().getResource(path);

            if (imageUrl != null) {
                iconCache.put(path, new ImageIcon(imageUrl));
            } else {
                String filename = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;

                // Updated to look in the "img/" directory instead of "icons/"
                URL alt = getClass().getClassLoader().getResource("img/" + filename);
                if (alt != null) {
                    iconCache.put(path, new ImageIcon(alt));
                } else {
                    try {
                        // Updated to look in the "src/img/" directory instead of "src/icons/"
                        java.io.File f = new java.io.File(System.getProperty("user.dir"), "src/img/" + filename);
                        if (f.exists()) {
                            iconCache.put(path, new ImageIcon(f.getAbsolutePath()));
                        } else {
                            throw new IllegalArgumentException("Asset not found: " + path);
                        }
                    } catch (SecurityException ex) {
                        throw new IllegalArgumentException("Asset not found: " + path, ex);
                    }
                }
            }
        }

        return iconCache.get(path);
    }

    /**
     * Empties the image cache.
     *
     * @return true if the cache contained items before clearing, false otherwise
     */
    public boolean clearCache() {
        boolean hadEntries = !iconCache.isEmpty();
        iconCache.clear();
        return hadEntries;
    }
}