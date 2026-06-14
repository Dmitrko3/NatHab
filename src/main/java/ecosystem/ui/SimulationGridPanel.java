package ecosystem.ui;

import ecosystem.engine.*;
import ecosystem.entities.base.AbstractEntity;

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
 * Custom panel that draws the simulation map and entities.
 */
public class SimulationGridPanel extends JPanel implements SimulationListener {

    private static final int CELL_SIZE = 32;

    private final Environment environment;
    private final EntityInfoPanel infoPanel;

    private Position selectedPosition;
    private AbstractEntity selectedEntity;
    private final Map<Class<?>, Image> iconCache = new HashMap<>();

    /**
     * Creates the grid panel for the simulation.
     *
     * @param environment the ecosystem environment
     * @param infoPanel the panel displaying entity details
     */
    public SimulationGridPanel(Environment environment, EntityInfoPanel infoPanel) {
        this.environment = environment;
        this.infoPanel = infoPanel;

        int panelWidth = Environment.WIDTH * CELL_SIZE;
        int panelHeight = Environment.HEIGHT * CELL_SIZE;

        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.WHITE);

        initializeMouseListeners();
    }

    /**
     * Sets up mouse interactions for tooltips and selection.
     */
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

    /**
     * Converts mouse pixel coordinates to grid coordinates.
     *
     * @param event the mouse event
     * @return the grid position, or null if out of bounds
     */
    private Position getPositionFromMouseEvent(MouseEvent event) {
        int gridX = event.getX() / CELL_SIZE;
        int gridY = event.getY() / CELL_SIZE;

        if (gridX < 0 || gridX >= Environment.WIDTH || gridY < 0 || gridY >= Environment.HEIGHT) {
            return null;
        }

        return new Position(gridX, gridY);
    }

    /**
     * Retrieves the entity at the given position.
     *
     * @param position the grid position
     * @return the entity, or null if empty
     */
    private AbstractEntity getEntityAt(Position position) {
        if (position == null) {
            return null;
        }

        return environment.getMapGrid().get(position);
    }

    /**
     * Paints the grid, entities, and selection highlight.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGrid(g);
        drawEntities(g);
        drawSelectedCell(g);
    }

    /**
     * Draws the grid lines based on environment dimensions.
     *
     * @param g the graphics context
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
     * Draws icons or text symbols for all living entities.
     *
     * @param g the graphics context
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

            Image img = iconCache.get(entity.getClass());
            if (img == null) {
                try {
                    String resourcePath = "img/" + entity.getClass().getSimpleName() + ".png";
                    img = AssetManager.getInstance().getIcon(resourcePath).getImage()
                            .getScaledInstance(CELL_SIZE - 4, CELL_SIZE - 4, Image.SCALE_SMOOTH);
                    iconCache.put(entity.getClass(), img);
                } catch (IllegalArgumentException ex) {
                    iconCache.put(entity.getClass(), null);
                    img = null;
                }
            }

            if (img != null) {
                g.drawImage(img, cellX + 2, cellY + 2, CELL_SIZE - 4, CELL_SIZE - 4, null);
            } else {
                String symbol = String.valueOf(entity.getSymbol());
                int textX = cellX + (CELL_SIZE - metrics.stringWidth(symbol)) / 2;
                int textY = cellY + ((CELL_SIZE - metrics.getHeight()) / 2) + metrics.getAscent();

                g.setColor(Color.BLACK);
                g.drawString(symbol, textX, textY);
            }
        }
    }

    /**
     * Highlights the currently selected grid cell.
     *
     * @param g the graphics context
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

    /**
     * Determines which cell should be highlighted.
     *
     * @return the position to highlight, or null if none
     */
    private Position getCurrentSelectedPosition() {
        if (selectedEntity != null && selectedEntity.isAlive()) {
            return selectedEntity.getPosition();
        }

        return selectedPosition;
    }

    /**
     * Updates the grid display when the simulation changes.
     *
     * @param environment the updated environment
     * @return true if updated successfully
     */
    @Override
    public boolean onSimulationUpdated(Environment environment) {
        if (selectedEntity != null && selectedEntity.isAlive()
                && environment.getEntitiesList().contains(selectedEntity)) {
            infoPanel.displayEntity(selectedEntity);
        } else if (selectedEntity != null) {
            selectedEntity = null;
            infoPanel.clearEntity();
        }

        repaint();
        return true;
    }
}