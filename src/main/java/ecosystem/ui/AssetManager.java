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
            URL imageUrl = getClass().getClassLoader().getResource(path);

            if (imageUrl == null) {
                throw new IllegalArgumentException("Asset not found: " + path);
            }

            iconCache.put(path, new ImageIcon(imageUrl));
        }

        return iconCache.get(path);
    }

    public void clearCache() {
        iconCache.clear();
    }
}