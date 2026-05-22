package ecosystem.ui;

import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton resource manager responsible for loading image assets once
 * and reusing them throughout the GUI.
 */
public class AssetManager {

    private static final AssetManager INSTANCE = new AssetManager();

    private final Map<String, ImageIcon> iconCache;

    private AssetManager() {
        this.iconCache = new HashMap<>();
    }

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    public ImageIcon getIcon(String path) {
        if (!iconCache.containsKey(path)) {
            // Try classpath first (expected location for packaged resources)
            URL imageUrl = getClass().getClassLoader().getResource(path);

            if (imageUrl != null) {
                iconCache.put(path, new ImageIcon(imageUrl));
            } else {
                // Fallback 1: try looking under a top-level "icons/" directory on the classpath
                String filename = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
                URL alt = getClass().getClassLoader().getResource("icons/" + filename);
                if (alt != null) {
                    iconCache.put(path, new ImageIcon(alt));
                } else {
                    // Fallback 2: attempt to load from the source tree when running from IDE
                    // (project root)/src/icons/<filename>
                    try {
                        java.io.File f = new java.io.File(System.getProperty("user.dir"), "src/icons/" + filename);
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

    public void clearCache() {
        iconCache.clear();
    }
}