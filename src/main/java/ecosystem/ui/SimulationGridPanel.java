package ecosystem.ui;

import ecosystem.core.*;
import ecosystem.entities.*;
import ecosystem.ui.*;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom panel responsible for drawing the simulation map.
 *
 * It dynamically reads the grid size from Environment.WIDTH and Environment.HEIGHT,
 * so the GUI always matches the model's actual dimensions.
 */
public class SimulationGridPanel extends JPanel implements SimulationListener {

    private static final int CELL_SIZE = 32;

    private final Environment environment;
    private final EntityInfoPanel infoPanel;

    private Position selectedPosition;
    private AbstractEntity selectedEntity;
    private final Map<Class<?>, Image> iconCache = new HashMap<>();

    public SimulationGridPanel(Environment environment, EntityInfoPanel infoPanel) {
        this.environment = environment;
        this.infoPanel = infoPanel;

        int panelWidth = Environment.WIDTH * CELL_SIZE;
        int panelHeight = Environment.HEIGHT * CELL_SIZE;

        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.WHITE);

        initializeMouseListeners();
    }

    private void initializeMouseListeners() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                Position position = getPositionFromMouseEvent(event);
                AbstractEntity entity = getEntityAt(position);

                if (entity != null) {
                    setToolTipText(entity.toString());
                } else {
                    setToolTipText(null);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                Position position = getPositionFromMouseEvent(event);
                AbstractEntity entity = getEntityAt(position);

                selectedPosition = position;
                selectedEntity = entity;

                if (entity != null) {
                    infoPanel.displayEntity(entity);
                } else {
                    infoPanel.clearEntity();
                }

                repaint();
            }
        });
    }


    private Position getPositionFromMouseEvent(MouseEvent event) {
        int gridX = event.getX() / CELL_SIZE;
        int gridY = event.getY() / CELL_SIZE;

        if (gridX < 0 || gridX >= Environment.WIDTH || gridY < 0 || gridY >= Environment.HEIGHT) {
            return null;
        }

        return new Position(gridX, gridY);
    }

    private AbstractEntity getEntityAt(Position position) {
        if (position == null) {
            return null;
        }

        return environment.getMapGrid().get(position);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGrid(g);
        drawEntities(g);
        drawSelectedCell(g);
    }

    /**
     * Draws the grid using the exact width and height from Environment.
     */
    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);

        for (int x = 0; x <= Environment.WIDTH; x++) {
            int pixelX = x * CELL_SIZE;
            g.drawLine(pixelX, 0, pixelX, Environment.HEIGHT * CELL_SIZE);
        }

        for (int y = 0; y <= Environment.HEIGHT; y++) {
            int pixelY = y * CELL_SIZE;
            g.drawLine(0, pixelY, Environment.WIDTH * CELL_SIZE, pixelY);
        }
    }

    /**
     * Draws each living entity inside its grid cell.
     */
    private void drawEntities(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics metrics = g.getFontMetrics();

        for (AbstractEntity entity : environment.getEntitiesList()) {
            if (!entity.isAlive()) {
                continue;
            }

            Position position = entity.getPosition();

            int cellX = position.getX() * CELL_SIZE;
            int cellY = position.getY() * CELL_SIZE;

            // Try to draw an icon named after the entity class: e.g. Lion.png, Rabbit.png
            Image img = iconCache.get(entity.getClass());
            if (img == null) {
                try {
                    // Resource path: package path where icons reside
                    String resourcePath = "ecosystem/ui/icons/" + entity.getClass().getSimpleName() + ".png";
                    img = AssetManager.getInstance().getIcon(resourcePath).getImage()
                            .getScaledInstance(CELL_SIZE - 4, CELL_SIZE - 4, Image.SCALE_SMOOTH);
                    iconCache.put(entity.getClass(), img);
                } catch (IllegalArgumentException ex) {
                    // Icon not found — cache a null marker to avoid repeated lookups
                    iconCache.put(entity.getClass(), null);
                    img = null;
                }
            }

            if (img != null) {
                // Draw image centered with a small margin
                g.drawImage(img, cellX + 2, cellY + 2, CELL_SIZE - 4, CELL_SIZE - 4, null);
            } else {
                // Fallback: draw symbol letter as before
                String symbol = String.valueOf(entity.getSymbol());
                int textX = cellX + (CELL_SIZE - metrics.stringWidth(symbol)) / 2;
                int textY = cellY + ((CELL_SIZE - metrics.getHeight()) / 2) + metrics.getAscent();

                g.setColor(Color.BLACK);
                g.drawString(symbol, textX, textY);
            }
        }
    }

    /**
     * Draws a visual highlight around the selected cell.
     */
    private void drawSelectedCell(Graphics g) {
        Position highlightPosition = getCurrentSelectedPosition();

        if (highlightPosition == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        int x = highlightPosition.getX() * CELL_SIZE;
        int y = highlightPosition.getY() * CELL_SIZE;

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        g2.dispose();
    }

    private Position getCurrentSelectedPosition() {
        if (selectedEntity != null && selectedEntity.isAlive()) {
            return selectedEntity.getPosition();
        }

        return selectedPosition;
    }


    /**
     * Called by SimulationEngine whenever the simulation updates.
     * Repaints the grid so the GUI shows the latest state.
     */
    @Override
    public void onSimulationUpdated(Environment environment) {
        // Ensure the selected entity is still present in the environment before
        // continuing to display it. If it's been removed/cleared, drop selection.
        if (selectedEntity != null && selectedEntity.isAlive()
                && environment.getEntitiesList().contains(selectedEntity)) {
            infoPanel.displayEntity(selectedEntity);
        } else if (selectedEntity != null) {
            selectedEntity = null;
            infoPanel.clearEntity();
        }

        repaint();
    }
}
