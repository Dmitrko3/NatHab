package ecosystem.core;

import ecosystem.entities.AbstractEntity;

import java.util.*;

/**
 * Represents the 2-D grid world.  Manages entity placement, lookup, and
 * spatial queries.
 *
 * <p>Invariant: every living entity appears both in {@code mapGrid} (keyed by
 * its current position) and in {@code entitiesList}.
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
     * Returns {@code true} when {@code pos} is inside the grid bounds AND no
     * living entity currently occupies that cell.
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
     * Returns all living entities whose Manhattan distance from {@code pos} is
     * at most 2, excluding any entity located exactly at {@code pos}.
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
     * Adds an entity to the world at its current position.
     *
     * @return {@code true} if the entity was placed successfully
     */
    public boolean addEntity(AbstractEntity entity) {
        if (entity == null) return false;
        if (!isPositionFree(entity.getPosition())) return false;
        mapGrid.put(entity.getPosition(), entity);
        entitiesList.add(entity);
        return true;
    }

    /**
     * Removes a (usually dead) entity from both the spatial map and the list.
     *
     * @return {@code true} if the entity was found and removed
     */
    public boolean removeEntity(AbstractEntity entity) {
        if (entity == null) return false;
        // Remove from spatial map only if the map still points to this entity
        mapGrid.remove(entity.getPosition(), entity);
        return entitiesList.remove(entity);
    }

    /**
     * Updates the spatial map when an entity moves from {@code oldPos} to
     * {@code newPos}.  Must be called <em>after</em> updating the entity's
     * own position field.
     */
    public void updateEntityPosition(AbstractEntity entity,
                                     Position oldPos, Position newPos) {
        mapGrid.remove(oldPos);
        mapGrid.put(newPos, entity);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns a read-only view of the entity list (snapshot-safe for iteration). */
    public List<AbstractEntity> getEntitiesList() {
        return Collections.unmodifiableList(entitiesList);
    }

    /** Returns a read-only view of the spatial map. */
    public Map<Position, AbstractEntity> getMapGrid() {
        return Collections.unmodifiableMap(mapGrid);
    }
}
