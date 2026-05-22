package ecosystem.core;

import ecosystem.entities.*;

import java.util.*;

/**
 * Represents the 2D world where entities live.
 *
 * <p>Keeps track of where entities are placed and helps find nearby entities.
 */
public class Environment {

    /** Width of the simulation grid in cells. */
    public static final int WIDTH  = 20;
    /** Height of the simulation grid in cells. */
    public static final int HEIGHT = 20;

    /** Spatial lookup: position → entity occupying that cell. */
    private final Map<Position, AbstractEntity> mapGrid;

    /** Flat list of all entities ever added (including dead ones until cleanup). */
    private final List<AbstractEntity> entitiesList;

    public Environment() {
        this.mapGrid       = new HashMap<>();
        this.entitiesList  = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Spatial helpers
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the position is inside the grid and the cell is free.
     */
    public boolean isPositionFree(Position pos) {
        if (pos.getX() < 0 || pos.getX() >= WIDTH
                || pos.getY() < 0 || pos.getY() >= HEIGHT) {
            return false;
        }
        AbstractEntity occupant = mapGrid.get(pos);
        // A cell is free if it is empty or its previous occupant is already dead
        return occupant == null || !occupant.isAlive();
    }

    /**
     * Returns all living entities that are close to the given position.
     */
    public List<AbstractEntity> getNearbyEntities(Position pos) {
        List<AbstractEntity> nearby = new ArrayList<>();
        for (AbstractEntity entity : entitiesList) {
            if (entity.isAlive()
                    && !entity.getPosition().equals(pos)
                    && entity.getPosition().distanceTo(pos) <= 2) {
                nearby.add(entity);
            }
        }
        return nearby;
    }

    // -------------------------------------------------------------------------
    // Entity management
    // -------------------------------------------------------------------------

    /**
     * Adds an entity to the world.
     *
     * @return {@code true} if the entity was added successfully
     */
    public boolean addEntity(AbstractEntity entity) {
        if (entity == null) return false;
        if (!isPositionFree(entity.getPosition())) return false;
        mapGrid.put(entity.getPosition(), entity);
        entitiesList.add(entity);
        return true;
    }

    /**
     * Adds an entity to the world.
     *
     * @return {@code true} if the entity was added successfully
     */
    public boolean removeEntity(AbstractEntity entity) {
        if (entity == null) return false;
        // Remove from spatial map only if the map still points to this entity
        mapGrid.remove(entity.getPosition(), entity);
        return entitiesList.remove(entity);
    }

    /**
     * Updates the entity's position in the map after it moves.
     */
    public void updateEntityPosition(AbstractEntity entity,
                                     Position oldPos, Position newPos) {
        mapGrid.remove(oldPos);
        mapGrid.put(newPos, entity);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns a read-only list of entities. */
    public List<AbstractEntity> getEntitiesList() {
        return Collections.unmodifiableList(entitiesList);
    }

    /** Returns a read-only view of the spatial map. */
    public Map<Position, AbstractEntity> getMapGrid() {
        return Collections.unmodifiableMap(mapGrid);
    }

    /**
     * Clears the world, removing all entities and freeing all cells.
     * Useful for resetting the simulation to an empty state.
     */
    public void clear() {
        mapGrid.clear();
        entitiesList.clear();
    }
}
