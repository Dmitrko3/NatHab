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
     * Checks if a position is free.
     * @return true if in-bounds and empty (or occupant is dead).
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
     * Returns a list of living entities within a 2-tile radius of the given position.
     *
     * @param pos The central position.
     * @return List of nearby entities.
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

    /** Updates the entity's position in the map after it moves.
     *@return true if the environment was updated successfully, false otherwise*/
    public boolean updateEntityPosition(AbstractEntity entity,
                                        Position oldPos, Position newPos) {
        if (entity == null || oldPos == null || newPos == null) return false;
        // If the old position mapping does not point to this entity, be defensive:
        AbstractEntity current = mapGrid.get(oldPos);
        if (current != null && current != entity) {
            // Someone else occupies oldPos; do not remove them.
            // Only allow placing into newPos if it's free.
            if (!isPositionFree(newPos)) return false;
        }

        // Remove oldPos only if it maps to this entity (avoid removing other occupants)
        mapGrid.remove(oldPos, entity);
        mapGrid.put(newPos, entity);
        return true;
    }

    /** * Clears the world of all entities.
     * * @return true if the environment was not already empty.
     */
    public boolean clear() {
        boolean hadEntries = !mapGrid.isEmpty() || !entitiesList.isEmpty();
        mapGrid.clear();
        entitiesList.clear();
        return hadEntries;
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

}
